import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import PdfPreviewModal from '../components/PdfPreviewModal';
import {
  ShieldCheck,
  CheckCircle,
  XCircle,
  Clock,
  Eye,
  Search,
  Filter,
  GraduationCap,
  ExternalLink,
  Award,
  AlertCircle
} from 'lucide-react';
import { Link } from 'react-router-dom';

export default function FacultyReview() {
  const [activeTab, setActiveTab] = useState('VERIFICATION'); // VERIFICATION or SEARCH
  const [pendingCerts, setPendingCerts] = useState([]);
  const [loadingCerts, setLoadingCerts] = useState(true);
  const [previewCert, setPreviewCert] = useState(null);

  // Review action modal state
  const [auditCert, setAuditCert] = useState(null);
  const [auditStatus, setAuditStatus] = useState('VERIFIED');
  const [auditRemarks, setAuditRemarks] = useState('');
  const [submittingAudit, setSubmittingAudit] = useState(false);
  const [message, setMessage] = useState('');

  // Talent Search state
  const [searchSkill, setSearchSkill] = useState('');
  const [searchDept, setSearchDept] = useState('');
  const [searchMinCgpa, setSearchMinCgpa] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loadingSearch, setLoadingSearch] = useState(false);

  const fetchPendingCerts = async () => {
    try {
      setLoadingCerts(true);
      const res = await api.get('/api/faculty/certifications/pending');
      setPendingCerts(res.data);
    } catch (err) {
      console.error('Failed to load pending certifications:', err);
    } finally {
      setLoadingCerts(false);
    }
  };

  const handleSearch = async (e) => {
    if (e) e.preventDefault();
    try {
      setLoadingSearch(true);
      const params = {};
      if (searchSkill.trim()) params.skill = searchSkill.trim();
      if (searchDept.trim()) params.dept = searchDept.trim();
      if (searchMinCgpa) params.minCgpa = Number(searchMinCgpa);

      const res = await api.get('/api/faculty/students/search', { params });
      setSearchResults(res.data);
    } catch (err) {
      console.error('Talent search failed:', err);
    } finally {
      setLoadingSearch(false);
    }
  };

  useEffect(() => {
    fetchPendingCerts();
    handleSearch();
  }, []);

  const openAuditDialog = (cert, status) => {
    setAuditCert(cert);
    setAuditStatus(status);
    setAuditRemarks(
      status === 'VERIFIED'
        ? `Verified against ${cert.issuer} verification registrar. Distinction verified.`
        : 'Proof illegible or incomplete credential information.'
    );
  };

  const submitAudit = async (e) => {
    e.preventDefault();
    if (!auditCert) return;
    try {
      setSubmittingAudit(true);
      await api.patch(`/api/faculty/certifications/${auditCert.id}/verify`, {
        status: auditStatus,
        remarks: auditRemarks,
      });

      setPendingCerts(pendingCerts.filter((c) => c.id !== auditCert.id));
      setMessage(`Certificate #${auditCert.id} successfully updated to ${auditStatus}.`);
      setAuditCert(null);
      setTimeout(() => setMessage(''), 4000);
    } catch (err) {
      alert('Verification update failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setSubmittingAudit(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Console Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center space-x-2">
            <ShieldCheck className="w-6 h-6 text-indigo-600" />
            <span>Faculty Verification & Talent Search Console</span>
          </h1>
          <p className="text-xs text-slate-500 mt-1">
            Audit student credential proofs, endorse master proficiencies, and discover placement candidates
          </p>
        </div>

        {/* Tab Switcher */}
        <div className="flex p-1 bg-slate-100 rounded-xl self-start sm:self-auto">
          <button
            onClick={() => setActiveTab('VERIFICATION')}
            className={`px-4 py-2 text-xs font-bold rounded-lg transition-all ${
              activeTab === 'VERIFICATION'
                ? 'bg-white text-indigo-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Pending Audits ({pendingCerts.length})
          </button>
          <button
            onClick={() => setActiveTab('SEARCH')}
            className={`px-4 py-2 text-xs font-bold rounded-lg transition-all ${
              activeTab === 'SEARCH'
                ? 'bg-white text-indigo-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Talent Search Pool
          </button>
        </div>
      </div>

      {message && (
        <div className="p-4 text-sm font-semibold text-emerald-800 bg-emerald-50 border border-emerald-200 rounded-2xl animate-fade-in flex items-center space-x-2">
          <CheckCircle className="w-4 h-4 text-emerald-600" />
          <span>{message}</span>
        </div>
      )}

      {/* Tab 1: Pending Verifications Queue */}
      {activeTab === 'VERIFICATION' && (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden space-y-4">
          <div className="p-6 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h3 className="text-base font-bold text-slate-900">
                Awaiting Verification ({pendingCerts.length})
              </h3>
              <p className="text-xs text-slate-500">
                Inspect binary files, verify credential IDs, and approve or reject with remarks
              </p>
            </div>
            <button
              onClick={fetchPendingCerts}
              className="text-xs font-semibold text-blue-600 hover:underline"
            >
              Refresh Queue
            </button>
          </div>

          {loadingCerts ? (
            <div className="p-12 text-center text-slate-500">
              <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
              <p className="text-sm font-medium">Loading audit queue...</p>
            </div>
          ) : pendingCerts.length === 0 ? (
            <div className="p-12 text-center space-y-2">
              <CheckCircle className="w-12 h-12 text-emerald-500 mx-auto" />
              <h3 className="text-base font-bold text-slate-700">Audit Queue Clear!</h3>
              <p className="text-xs text-slate-400">
                All submitted student certificates have been verified or resolved.
              </p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-slate-600">
                <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold uppercase tracking-wider text-slate-500">
                  <tr>
                    <th scope="col" className="px-6 py-4">Student</th>
                    <th scope="col" className="px-6 py-4">Certificate Details</th>
                    <th scope="col" className="px-6 py-4">Credential ID</th>
                    <th scope="col" className="px-6 py-4">Submitted</th>
                    <th scope="col" className="px-6 py-4">Proof Document</th>
                    <th scope="col" className="px-6 py-4 text-right">Audit Decision</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  {pendingCerts.map((cert) => (
                    <tr key={cert.id} className="hover:bg-slate-50/60 transition-colors">
                      <td className="px-6 py-4">
                        <div className="font-bold text-slate-900">{cert.studentName}</div>
                        <div className="text-xs text-indigo-600 font-mono">
                          {cert.studentRollNumber} • {cert.studentDepartment}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="font-bold text-slate-800">{cert.title}</div>
                        <div className="text-xs text-slate-400">{cert.issuer}</div>
                      </td>
                      <td className="px-6 py-4 font-mono text-xs text-slate-600">
                        {cert.credentialId || '—'}
                      </td>
                      <td className="px-6 py-4 text-xs text-slate-500">
                        {cert.createdAt ? new Date(cert.createdAt).toLocaleDateString() : 'Recent'}
                      </td>
                      <td className="px-6 py-4">
                        <button
                          onClick={() => setPreviewCert(cert)}
                          className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold text-indigo-700 bg-indigo-50 hover:bg-indigo-100 transition-colors"
                        >
                          <Eye className="w-3.5 h-3.5" />
                          <span>View Proof File</span>
                        </button>
                      </td>
                      <td className="px-6 py-4 text-right space-x-2">
                        <button
                          onClick={() => openAuditDialog(cert, 'VERIFIED')}
                          className="inline-flex items-center space-x-1 px-3 py-1.5 rounded-lg text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 shadow-sm transition-all"
                        >
                          <CheckCircle className="w-3.5 h-3.5" />
                          <span>Approve</span>
                        </button>
                        <button
                          onClick={() => openAuditDialog(cert, 'REJECTED')}
                          className="inline-flex items-center space-x-1 px-3 py-1.5 rounded-lg text-xs font-bold text-rose-700 bg-rose-50 hover:bg-rose-100 border border-rose-200 transition-colors"
                        >
                          <XCircle className="w-3.5 h-3.5" />
                          <span>Reject</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Tab 2: Talent Discovery Search Console */}
      {activeTab === 'SEARCH' && (
        <div className="space-y-6">
          {/* Multi-Filter Search Bar */}
          <form
            onSubmit={handleSearch}
            className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm grid grid-cols-1 sm:grid-cols-4 gap-4 items-end"
          >
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Filter by Technical Skill
              </label>
              <div className="relative">
                <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="text"
                  value={searchSkill}
                  onChange={(e) => setSearchSkill(e.target.value)}
                  placeholder="e.g. React, Spring Boot"
                  className="w-full pl-9 pr-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Department
              </label>
              <select
                value={searchDept}
                onChange={(e) => setSearchDept(e.target.value)}
                className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white"
              >
                <option value="">All Departments</option>
                <option value="CSE">CSE</option>
                <option value="IT">IT</option>
                <option value="ECE">ECE</option>
                <option value="AI&DS">AI & DS</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Minimum CGPA
              </label>
              <input
                type="number"
                step="0.1"
                min="0"
                max="10"
                value={searchMinCgpa}
                onChange={(e) => setSearchMinCgpa(e.target.value)}
                placeholder="e.g. 8.0"
                className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <button
              type="submit"
              disabled={loadingSearch}
              className="w-full flex items-center justify-center space-x-2 py-2.5 px-4 rounded-xl text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-md shadow-indigo-500/20 transition-all"
            >
              <Filter className="w-4 h-4" />
              <span>{loadingSearch ? 'Querying...' : 'Filter Talent Pool'}</span>
            </button>
          </form>

          {/* Search Result Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {searchResults.map((student) => (
              <div
                key={student.id}
                className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between space-y-4"
              >
                <div>
                  <div className="flex items-start justify-between">
                    <div>
                      <h4 className="font-bold text-slate-900 text-base">{student.fullName}</h4>
                      <p className="text-xs font-mono text-indigo-600">
                        {student.rollNumber} • {student.department}
                      </p>
                    </div>
                    <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-indigo-50 text-indigo-700 border border-indigo-200">
                      CGPA {Number(student.cgpa).toFixed(2)}
                    </span>
                  </div>

                  <div className="mt-4">
                    <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider mb-1.5">
                      Technical Skills
                    </p>
                    <div className="flex flex-wrap gap-1.5">
                      {student.skills && student.skills.length > 0 ? (
                        student.skills.map((s, idx) => (
                          <span
                            key={idx}
                            className="px-2 py-0.5 rounded-lg text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200"
                          >
                            {s}
                          </span>
                        ))
                      ) : (
                        <span className="text-xs text-slate-400 italic">No skills tagged</span>
                      )}
                    </div>
                  </div>
                </div>

                <div className="pt-4 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-xs font-semibold text-emerald-600 flex items-center">
                    <Award className="w-3.5 h-3.5 mr-1" />
                    {student.verifiedCertCount} Verified Credential{student.verifiedCertCount === 1 ? '' : 's'}
                  </span>
                  <Link
                    to={`/portfolio/${student.rollNumber}`}
                    target="_blank"
                    className="inline-flex items-center space-x-1 text-xs font-bold text-indigo-600 hover:text-indigo-800"
                  >
                    <span>View Public Portfolio</span>
                    <ExternalLink className="w-3 h-3" />
                  </Link>
                </div>
              </div>
            ))}
          </div>

          {searchResults.length === 0 && !loadingSearch && (
            <div className="p-12 text-center bg-white rounded-2xl border border-slate-200 text-slate-500">
              <Search className="w-10 h-10 mx-auto text-slate-300 mb-2" />
              <p className="font-semibold text-sm">No students matched the query filters.</p>
              <p className="text-xs text-slate-400 mt-1">Try lowering the minimum CGPA or clearing skill keywords.</p>
            </div>
          )}
        </div>
      )}

      {/* Audit Decision Modal */}
      {auditCert && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 border border-slate-200 space-y-4">
            <h3 className="text-lg font-bold text-slate-900">
              Audit Submission: {auditCert.title}
            </h3>
            <p className="text-xs text-slate-500">
              Student: {auditCert.studentName} ({auditCert.studentRollNumber}) • Issuer: {auditCert.issuer}
            </p>

            <form onSubmit={submitAudit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                  Audit Verdict
                </label>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    type="button"
                    onClick={() => setAuditStatus('VERIFIED')}
                    className={`py-2 text-xs font-bold rounded-xl border transition-all ${
                      auditStatus === 'VERIFIED'
                        ? 'bg-emerald-600 text-white border-emerald-600'
                        : 'bg-slate-50 text-slate-600 border-slate-200'
                    }`}
                  >
                    Approve (VERIFIED)
                  </button>
                  <button
                    type="button"
                    onClick={() => setAuditStatus('REJECTED')}
                    className={`py-2 text-xs font-bold rounded-xl border transition-all ${
                      auditStatus === 'REJECTED'
                        ? 'bg-rose-600 text-white border-rose-600'
                        : 'bg-slate-50 text-slate-600 border-slate-200'
                    }`}
                  >
                    Reject (REJECTED)
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                  Faculty Audit Remarks & Feedback *
                </label>
                <textarea
                  required
                  rows={3}
                  value={auditRemarks}
                  onChange={(e) => setAuditRemarks(e.target.value)}
                  placeholder="Record verification notes, roll check, score details..."
                  className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="flex items-center justify-end space-x-3 pt-3 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setAuditCert(null)}
                  className="px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submittingAudit}
                  className="px-5 py-2 text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl shadow-md disabled:opacity-50"
                >
                  {submittingAudit ? 'Recording Audit...' : 'Confirm Audit Decision'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

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
