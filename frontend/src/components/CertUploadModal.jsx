import React, { useState, useRef } from 'react';
import { UploadCloud, X, FileText, AlertCircle, Check } from 'lucide-react';
import api from '../api/axiosConfig';

export default function CertUploadModal({ isOpen, onClose, studentId, onUploadSuccess }) {
  const [file, setFile] = useState(null);
  const [title, setTitle] = useState('');
  const [issuer, setIssuer] = useState('');
  const [credentialId, setCredentialId] = useState('');
  const [issueDate, setIssueDate] = useState('');
  const [dragOver, setDragOver] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const fileInputRef = useRef(null);

  if (!isOpen) return null;

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragover' || e.type === 'dragenter') {
      setDragOver(true);
    } else if (e.type === 'dragleave') {
      setDragOver(false);
    }
  };

  const validateAndSetFile = (selectedFile) => {
    setError('');
    if (!selectedFile) return;

    const allowedTypes = ['application/pdf', 'image/png', 'image/jpeg', 'image/jpg'];
    if (!allowedTypes.includes(selectedFile.type)) {
      setError('Invalid file type. Only PDF, PNG, or JPEG documents are permitted.');
      return;
    }

    if (selectedFile.size > 5 * 1024 * 1024) {
      setError('File size exceeds the 5 MB limit.');
      return;
    }

    setFile(selectedFile);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragOver(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      validateAndSetFile(e.dataTransfer.files[0]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select or drop a certificate document.');
      return;
    }
    if (!title.trim() || !issuer.trim()) {
      setError('Certificate title and issuer are required.');
      return;
    }

    try {
      setLoading(true);
      setError('');

      const formData = new FormData();
      formData.append('file', file);
      formData.append('title', title.trim());
      formData.append('issuer', issuer.trim());
      if (credentialId.trim()) formData.append('credentialId', credentialId.trim());
      if (issueDate) formData.append('issueDate', issueDate);

      const res = await api.post(`/api/students/${studentId}/certifications`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      onUploadSuccess(res.data);
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to upload certificate. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full overflow-hidden border border-slate-200">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 bg-slate-50/50">
          <div>
            <h3 className="text-lg font-bold text-slate-800">Upload Certificate Proof</h3>
            <p className="text-xs text-slate-500">Submit institutional credential for faculty verification</p>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="flex items-center space-x-2 p-3 text-sm text-rose-700 bg-rose-50 border border-rose-200 rounded-xl">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Drag & Drop Zone */}
          <div
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            className={`border-2 border-dashed rounded-xl p-6 text-center cursor-pointer transition-colors ${
              dragOver
                ? 'border-blue-500 bg-blue-50/50'
                : file
                ? 'border-emerald-400 bg-emerald-50/30'
                : 'border-slate-300 hover:border-blue-400 bg-slate-50/50'
            }`}
          >
            <input
              type="file"
              ref={fileInputRef}
              onChange={(e) => validateAndSetFile(e.target.files?.[0])}
              accept=".pdf,.png,.jpg,.jpeg"
              className="hidden"
            />
            {file ? (
              <div className="flex items-center justify-center space-x-3 text-emerald-800">
                <FileText className="w-8 h-8 text-emerald-600" />
                <div className="text-left">
                  <p className="font-semibold text-sm truncate max-w-xs">{file.name}</p>
                  <p className="text-xs text-emerald-600">{(file.size / 1024 / 1024).toFixed(2)} MB • Ready to upload</p>
                </div>
              </div>
            ) : (
              <div className="space-y-1">
                <UploadCloud className="w-10 h-10 mx-auto text-blue-500 mb-2" />
                <p className="text-sm font-semibold text-slate-700">
                  Click to browse or drag and drop proof file
                </p>
                <p className="text-xs text-slate-400">
                  Accepts PDF, PNG, or JPEG up to 5 MB
                </p>
              </div>
            )}
          </div>

          {/* Form Inputs */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              Certificate Title *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. AWS Certified Cloud Practitioner"
              className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Issuing Organization *
              </label>
              <input
                type="text"
                required
                value={issuer}
                onChange={(e) => setIssuer(e.target.value)}
                placeholder="e.g. AWS, NPTEL, Coursera"
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Credential ID
              </label>
              <input
                type="text"
                value={credentialId}
                onChange={(e) => setCredentialId(e.target.value)}
                placeholder="e.g. CERT-98234-A"
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              Issue Date
            </label>
            <input
              type="date"
              value={issueDate}
              onChange={(e) => setIssueDate(e.target.value)}
              className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end space-x-3 pt-3 border-t border-slate-100">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="px-4 py-2 text-sm font-medium text-slate-600 hover:text-slate-800 hover:bg-slate-100 rounded-xl transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-5 py-2 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-md shadow-blue-500/20 disabled:opacity-50 transition-all"
            >
              {loading ? 'Uploading...' : 'Submit for Verification'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
