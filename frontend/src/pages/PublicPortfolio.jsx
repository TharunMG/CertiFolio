import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import SkillBadge from '../components/SkillBadge';
import PdfPreviewModal from '../components/PdfPreviewModal';
import {
  ShieldCheck,
  Download,
  Award,
  GraduationCap,
  ExternalLink,
  Github,
  Linkedin,
  CheckCircle2,
  Calendar,
  Building,
  Eye,
  FileCheck2
} from 'lucide-react';

export default function PublicPortfolio() {
  const { rollNo } = useParams();
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [downloadingPdf, setDownloadingPdf] = useState(false);
  const [previewCert, setPreviewCert] = useState(null);

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        setLoading(true);
        setError('');
        const res = await api.get(`/api/portfolio/${rollNo}`);
        setPortfolio(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Portfolio not found for roll number: ' + rollNo);
      } finally {
        setLoading(false);
      }
    };

    if (rollNo) {
      fetchPortfolio();
    }
  }, [rollNo]);

  const handleDownloadPdf = async () => {
    try {
      setDownloadingPdf(true);
      const response = await api.get(`/api/portfolio/${rollNo}/pdf`, {
        responseType: 'blob',
      });

      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${rollNo.toUpperCase()}_Official_Portfolio.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert('Failed to download PDF transcript. Please try again.');
    } finally {
      setDownloadingPdf(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="text-center space-y-3">
          <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto" />
          <p className="text-sm font-semibold text-slate-600">Verifying Institutional Registry...</p>
        </div>
      </div>
    );
  }

  if (error || !portfolio) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white p-8 rounded-2xl shadow-xl border border-slate-200 max-w-md w-full text-center space-y-4">
          <div className="w-12 h-12 bg-rose-50 text-rose-600 rounded-2xl flex items-center justify-center mx-auto">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <h2 className="text-xl font-bold text-slate-800">Portfolio Not Found</h2>
          <p className="text-xs text-slate-500">{error}</p>
          <Link
            to="/login"
            className="inline-block px-4 py-2 text-xs font-semibold text-white bg-blue-600 rounded-xl"
          >
            Return to Home
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 via-white to-slate-50 pb-16">
      {/* Official Registry Top Banner */}
      <div className="bg-slate-900 text-white border-b border-slate-800 py-3">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 flex items-center justify-between text-xs">
          <div className="flex items-center space-x-2">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
            <span className="font-semibold tracking-wide text-slate-300">
              CertiFolio Cryptographically Verified Registry
            </span>
          </div>
          <span className="text-slate-400 font-mono hidden sm:inline">
            Status: SQL-Level Filtered (VERIFIED ONLY)
          </span>
        </div>
      </div>

      <main className="max-w-6xl mx-auto px-4 sm:px-6 mt-8 space-y-8">
        {/* Student Profile Hero Card */}
        <div className="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-sm relative overflow-hidden">
          <div className="absolute top-0 right-0 w-96 h-96 bg-blue-500/5 rounded-full blur-3xl -mr-20 -mt-20 pointer-events-none" />

          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 relative">
            <div className="flex items-center space-x-5">
              <div className="w-20 h-20 rounded-2xl bg-gradient-to-tr from-blue-700 via-blue-600 to-indigo-600 text-white font-extrabold text-3xl flex items-center justify-center shadow-lg shadow-blue-500/25">
                {portfolio.fullName.charAt(0)}
              </div>
              <div className="space-y-1">
                <div className="flex items-center space-x-2">
                  <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
                    {portfolio.fullName}
                  </h1>
                  <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800 border border-emerald-300">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    <span>Verified Candidate</span>
                  </span>
                </div>
                <p className="text-sm text-slate-500 font-medium">
                  {portfolio.department} Engineering • Year {portfolio.yearOfStudy} • Roll No:{' '}
                  <span className="font-mono text-slate-700 font-bold">{portfolio.rollNumber}</span>
                </p>
                <div className="flex items-center space-x-4 pt-1 text-xs">
                  <span className="font-bold text-slate-800 bg-slate-100 px-3 py-1 rounded-lg border border-slate-200">
                    CGPA: {Number(portfolio.cgpa).toFixed(2)} / 10.0
                  </span>
                  {portfolio.githubUrl && (
                    <a
                      href={portfolio.githubUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center space-x-1 text-slate-600 hover:text-blue-600 font-semibold"
                    >
                      <Github className="w-3.5 h-3.5" />
                      <span>GitHub</span>
                    </a>
                  )}
                  {portfolio.linkedinUrl && (
                    <a
                      href={portfolio.linkedinUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center space-x-1 text-slate-600 hover:text-blue-600 font-semibold"
                    >
                      <Linkedin className="w-3.5 h-3.5" />
                      <span>LinkedIn</span>
                    </a>
                  )}
                </div>
              </div>
            </div>

            {/* Blueprint Spec: Download Official Portfolio PDF Button */}
            <button
              onClick={handleDownloadPdf}
              disabled={downloadingPdf}
              className="inline-flex items-center justify-center space-x-2 px-6 py-3.5 rounded-2xl text-sm font-bold text-white bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-500/25 transition-all self-start md:self-auto disabled:opacity-50"
            >
              <Download className="w-4 h-4" />
              <span>{downloadingPdf ? 'Generating PDF...' : 'Download Official Portfolio PDF'}</span>
            </button>
          </div>
        </div>

        {/* Section 1: Endorsed Skills Matrix */}
        <section className="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900 tracking-tight flex items-center space-x-2">
                <GraduationCap className="w-5 h-5 text-blue-600" />
                <span>Verified Technical Proficiencies</span>
              </h2>
              <p className="text-xs text-slate-500">
                Skills attached by candidate and validated with code repositories
              </p>
            </div>
            <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
              {portfolio.skills?.length || 0} Skills Endorsed
            </span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3 pt-2">
            {portfolio.skills && portfolio.skills.length > 0 ? (
              portfolio.skills.map((skill) => (
                <SkillBadge key={skill.id} skill={skill} canDelete={false} />
              ))
            ) : (
              <p className="text-xs text-slate-400 italic">No skills endorsed on record.</p>
            )}
          </div>
        </section>

        {/* Section 2: Faculty-Verified Certifications */}
        <section className="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-sm space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900 tracking-tight flex items-center space-x-2">
                <FileCheck2 className="w-5 h-5 text-emerald-600" />
                <span>Faculty-Audited Credentials & Certifications</span>
              </h2>
              <p className="text-xs text-slate-500">
                Tamper-resistant credentials audited by institutional reviewers
              </p>
            </div>
            <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
              {portfolio.verifiedCertificates?.length || 0} Verified Credentials
            </span>
          </div>

          {portfolio.verifiedCertificates && portfolio.verifiedCertificates.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {portfolio.verifiedCertificates.map((cert) => (
                <div
                  key={cert.id}
                  className="p-5 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-slate-50 transition-colors flex flex-col justify-between space-y-3"
                >
                  <div className="space-y-1">
                    <div className="flex items-start justify-between">
                      <h4 className="font-bold text-slate-900 text-sm leading-snug">
                        {cert.title}
                      </h4>
                      <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-emerald-100 text-emerald-800 border border-emerald-300">
                        <CheckCircle2 className="w-3 h-3" />
                        <span>VERIFIED</span>
                      </span>
                    </div>
                    <p className="text-xs font-semibold text-blue-600 flex items-center space-x-1">
                      <Building className="w-3 h-3" />
                      <span>{cert.issuer}</span>
                      {cert.credentialId && (
                        <span className="text-slate-400 font-mono">({cert.credentialId})</span>
                      )}
                    </p>
                    {cert.issueDate && (
                      <p className="text-[11px] text-slate-400 flex items-center space-x-1">
                        <Calendar className="w-3 h-3" />
                        <span>Issued: {cert.issueDate}</span>
                      </p>
                    )}
                  </div>

                  {cert.remarks && (
                    <div className="p-3 bg-white rounded-xl border border-slate-200 text-xs text-slate-700">
                      <span className="font-bold text-slate-800">Faculty Audit Remarks: </span>
                      {cert.remarks}
                    </div>
                  )}

                  <div className="pt-2 flex items-center justify-between border-t border-slate-200/80">
                    <span className="text-[10px] text-slate-400">
                      Audit Timestamp: {cert.verifiedAt ? new Date(cert.verifiedAt).toLocaleDateString() : 'Audited'}
                    </span>
                    <button
                      onClick={() => setPreviewCert(cert)}
                      className="inline-flex items-center space-x-1 text-xs font-bold text-blue-600 hover:text-blue-800"
                    >
                      <Eye className="w-3.5 h-3.5" />
                      <span>Inspect Proof Document</span>
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="p-8 text-center border border-slate-200 rounded-2xl bg-slate-50">
              <Award className="w-8 h-8 text-slate-400 mx-auto mb-2" />
              <p className="text-sm font-semibold text-slate-600">
                No faculty-verified certificates on public display yet.
              </p>
              <p className="text-xs text-slate-400 mt-1">
                Submissions currently under pending audit are shielded from the public portfolio.
              </p>
            </div>
          )}
        </section>

        {/* Viva Standout Tip Box */}
        <div className="p-5 rounded-2xl bg-blue-50/60 border border-blue-200 text-xs text-slate-700 flex items-start space-x-3">
          <ShieldCheck className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
          <div className="space-y-1">
            <p className="font-bold text-blue-900">Institutional Authenticity Guarantee</p>
            <p className="text-slate-600 leading-relaxed">
              CertiFolio prevents forged credentials by enforcing a two-tier safeguard: (1) All public portfolio endpoints
              strictly execute SQL-level queries where <code className="bg-blue-100 px-1 py-0.5 rounded text-blue-800 font-mono">status = 'VERIFIED'</code>, and
              (2) Each record maintains an immutable institutional review timestamp alongside signed faculty remarks.
            </p>
          </div>
        </div>
      </main>

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
