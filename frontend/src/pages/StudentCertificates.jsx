import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosConfig';
import CertUploadModal from '../components/CertUploadModal';
import PdfPreviewModal from '../components/PdfPreviewModal';
import {
  Award,
  UploadCloud,
  FileText,
  Clock,
  CheckCircle,
  XCircle,
  Eye,
  ExternalLink,
  MessageSquareQuote
} from 'lucide-react';

export default function StudentCertificates() {
  const { user } = useAuth();
  const [certificates, setCertificates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [previewCert, setPreviewCert] = useState(null);

  const studentId = user?.studentProfileId;

  const fetchCertificates = async () => {
    if (!studentId) return;
    try {
      setLoading(true);
      const res = await api.get(`/api/students/${studentId}/certifications`);
      setCertificates(res.data);
    } catch (err) {
      console.error('Failed to load certificates:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCertificates();
  }, [studentId]);

  // Blueprint Semantic Tailwind Tokens
  const renderStatusPill = (status) => {
    switch (status) {
      case 'VERIFIED':
        return (
          <span className="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800 border border-emerald-300">
            <CheckCircle className="w-3.5 h-3.5" />
            <span>VERIFIED</span>
          </span>
        );
      case 'REJECTED':
        return (
          <span className="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-bold bg-rose-100 text-rose-800 border border-rose-300">
            <XCircle className="w-3.5 h-3.5" />
            <span>REJECTED</span>
          </span>
        );
      case 'PENDING':
      default:
        return (
          <span className="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-100 text-amber-800 border border-amber-300">
            <Clock className="w-3.5 h-3.5" />
            <span>PENDING AUDIT</span>
          </span>
        );
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center space-x-2">
            <Award className="w-6 h-6 text-blue-600" />
            <span>Certificate Repository & Status Tracker</span>
          </h1>
          <p className="text-xs text-slate-500 mt-1">
            Track verification audits, inspect faculty remarks, and upload course proofs
          </p>
        </div>

        <button
          onClick={() => setIsUploadOpen(true)}
          className="inline-flex items-center space-x-2 px-4 py-2.5 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-md shadow-blue-500/20 transition-all self-start sm:self-auto"
        >
          <UploadCloud className="w-4 h-4" />
          <span>Upload New Certificate</span>
        </button>
      </div>

      {/* Certificates Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-slate-500">
            <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
            <p className="text-sm font-medium">Fetching certificates...</p>
          </div>
        ) : certificates.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <FileText className="w-12 h-12 text-slate-300 mx-auto" />
            <h3 className="text-base font-bold text-slate-700">No Certificates Submitted Yet</h3>
            <p className="text-xs text-slate-400 max-w-sm mx-auto">
              Upload certificates from NPTEL, Coursera, AWS, or college hackathons to get them verified.
            </p>
            <button
              onClick={() => setIsUploadOpen(true)}
              className="mt-2 inline-flex items-center space-x-2 px-4 py-2 text-xs font-bold text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-xl transition-colors"
            >
              <UploadCloud className="w-4 h-4" />
              <span>Submit First Certificate</span>
            </button>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold uppercase tracking-wider text-slate-500">
                <tr>
                  <th scope="col" className="px-6 py-4">Title & Issuer</th>
                  <th scope="col" className="px-6 py-4">Credential ID</th>
                  <th scope="col" className="px-6 py-4">Issue Date</th>
                  <th scope="col" className="px-6 py-4">Status</th>
                  <th scope="col" className="px-6 py-4">Faculty Audit Remarks</th>
                  <th scope="col" className="px-6 py-4 text-right">Proof File</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-medium">
                {certificates.map((cert) => (
                  <tr key={cert.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-bold text-slate-900">{cert.title}</div>
                      <div className="text-xs text-slate-400">{cert.issuer}</div>
                    </td>
                    <td className="px-6 py-4 font-mono text-xs text-slate-600">
                      {cert.credentialId || '—'}
                    </td>
                    <td className="px-6 py-4 text-xs text-slate-600">
                      {cert.issueDate || '—'}
                    </td>
                    <td className="px-6 py-4">
                      {renderStatusPill(cert.status)}
                    </td>
                    <td className="px-6 py-4 text-xs">
                      {cert.remarks ? (
                        <div className="flex items-start space-x-1.5 text-slate-700 bg-slate-50 p-2 rounded-lg border border-slate-200">
                          <MessageSquareQuote className="w-3.5 h-3.5 text-blue-600 flex-shrink-0 mt-0.5" />
                          <span>{cert.remarks}</span>
                        </div>
                      ) : (
                        <span className="text-slate-400 italic">No remarks recorded</span>
                      )}
                      {cert.verifiedAt && (
                        <p className="text-[10px] text-slate-400 mt-1">
                          Audited on: {new Date(cert.verifiedAt).toLocaleDateString()}
                        </p>
                      )}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => setPreviewCert(cert)}
                        className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold text-blue-600 hover:text-blue-700 bg-blue-50 hover:bg-blue-100 transition-colors"
                      >
                        <Eye className="w-3.5 h-3.5" />
                        <span>Inspect Proof</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Upload Modal */}
      <CertUploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
        studentId={studentId}
        onUploadSuccess={(newCert) => {
          setCertificates([newCert, ...certificates]);
        }}
      />

      {/* PDF/Proof Preview Modal */}
      {previewCert && (
        <PdfPreviewModal
          isOpen={!!previewCert}
          onClose={() => setPreviewCert(null)}
          fileUrl={previewCert.fileUrl}
          title={`${previewCert.title} — ${previewCert.issuer}`}
        />
      )}
    </div>
  );
}
