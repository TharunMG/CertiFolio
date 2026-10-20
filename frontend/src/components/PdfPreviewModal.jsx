import React from 'react';
import { X, ExternalLink, FileText } from 'lucide-react';

export default function PdfPreviewModal({ isOpen, onClose, fileUrl, title }) {
  if (!isOpen || !fileUrl) return null;

  const isPdf = fileUrl.toLowerCase().endsWith('.pdf');
  const fullUrl = fileUrl.startsWith('http') ? fileUrl : `http://localhost:8080${fileUrl}`;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/70 backdrop-blur-sm animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full h-[85vh] flex flex-col overflow-hidden border border-slate-200">
        {/* Modal Top Bar */}
        <div className="flex items-center justify-between px-6 py-3.5 border-b border-slate-200 bg-slate-50">
          <div className="flex items-center space-x-2.5 truncate max-w-lg">
            <div className="p-1.5 bg-blue-100 text-blue-700 rounded-lg">
              <FileText className="w-4 h-4" />
            </div>
            <span className="font-bold text-sm text-slate-800 truncate">
              {title || 'Certificate Proof Preview'}
            </span>
          </div>

          <div className="flex items-center space-x-2">
            <a
              href={fullUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center space-x-1 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:text-blue-600 bg-white hover:bg-blue-50 border border-slate-300 rounded-lg transition-colors"
            >
              <ExternalLink className="w-3.5 h-3.5" />
              <span>Open in New Tab</span>
            </a>
            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-slate-600 hover:bg-slate-200 rounded-lg transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Modal Content Frame */}
        <div className="flex-1 bg-slate-100 p-2 overflow-auto flex items-center justify-center">
          {isPdf ? (
            <iframe
              src={fullUrl}
              title={title}
              className="w-full h-full rounded-lg border border-slate-300 bg-white"
            />
          ) : (
            <div className="max-w-full max-h-full flex items-center justify-center p-4">
              <img
                src={fullUrl}
                alt={title}
                className="max-w-full max-h-[75vh] object-contain rounded-lg shadow-md border border-slate-200"
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
