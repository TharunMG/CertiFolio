import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosConfig';
import SkillBadge from '../components/SkillBadge';
import CertUploadModal from '../components/CertUploadModal';
import {
  GraduationCap,
  Award,
  Code2,
  Plus,
  ExternalLink,
  UploadCloud,
  CheckCircle2,
  AlertTriangle,
  Sparkles,
  BookOpen
} from 'lucide-react';
import { Link } from 'react-router-dom';

export default function StudentDashboard() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [skills, setSkills] = useState([]);
  const [certificates, setCertificates] = useState([]);
  const [masterSkills, setMasterSkills] = useState([]);
  const [selectedSkillId, setSelectedSkillId] = useState('');
  const [selectedProficiency, setSelectedProficiency] = useState('BEGINNER');
  const [projectLink, setProjectLink] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState('');
  const [actionSuccess, setActionSuccess] = useState('');

  const studentId = user?.studentProfileId;

  const fetchData = async () => {
    if (!studentId) return;
    try {
      setLoading(true);
      const [profileRes, skillsRes, certsRes, masterRes] = await Promise.all([
        api.get(`/api/students/${studentId}/profile`),
        api.get(`/api/students/${studentId}/skills`),
        api.get(`/api/students/${studentId}/certifications`),
        api.get('/api/skills'),
      ]);

      setProfile(profileRes.data);
      setSkills(skillsRes.data);
      setCertificates(certsRes.data);
      setMasterSkills(masterRes.data);
    } catch (err) {
      console.error('Failed to load student data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [studentId]);

  const handleAddSkill = async (e) => {
    e.preventDefault();
    if (!selectedSkillId) return;
    setActionError('');
    setActionSuccess('');

    try {
      const res = await api.post(`/api/students/${studentId}/skills`, {
        skillId: Number(selectedSkillId),
        proficiency: selectedProficiency,
        projectLink: projectLink.trim() || null,
      });

      setSkills([...skills, res.data]);
      setSelectedSkillId('');
      setProjectLink('');
      setActionSuccess('Skill attached to your profile successfully!');
      setTimeout(() => setActionSuccess(''), 4000);
    } catch (err) {
      setActionError(err.response?.data?.message || 'Failed to attach skill');
      setTimeout(() => setActionError(''), 4000);
    }
  };

  const handleDeleteSkill = async (skillId) => {
    try {
      await api.delete(`/api/students/${studentId}/skills/${skillId}`);
      setSkills(skills.filter((s) => s.id !== skillId));
      setActionSuccess('Skill removed.');
      setTimeout(() => setActionSuccess(''), 3000);
    } catch (err) {
      setActionError('Could not remove skill.');
    }
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-12 text-center text-slate-500">
        <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-sm font-medium">Loading Academic Registry...</p>
      </div>
    );
  }

  const verifiedCertCount = certificates.filter((c) => c.status === 'VERIFIED').length;
  const pendingCertCount = certificates.filter((c) => c.status === 'PENDING').length;

  // Filter out already attached skills from the dropdown
  const attachedSkillIds = new Set(skills.map((s) => s.skillId));
  const availableCatalogSkills = masterSkills.filter((s) => !attachedSkillIds.has(s.id));

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Student Welcome Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div className="flex items-center space-x-4">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-blue-600 to-indigo-700 flex items-center justify-center text-white text-xl font-bold shadow-md shadow-blue-500/20">
            {profile?.fullName?.charAt(0) || 'S'}
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
                {profile?.fullName}
              </h1>
              <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-blue-100 text-blue-800 border border-blue-200">
                {profile?.rollNumber}
              </span>
            </div>
            <p className="text-xs text-slate-500 mt-1">
              Department of {profile?.department} • Year {profile?.yearOfStudy} • Student Skill Registry
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-3">
          <button
            onClick={() => setIsModalOpen(true)}
            className="inline-flex items-center space-x-2 px-4 py-2.5 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-md shadow-blue-500/20 transition-all"
          >
            <UploadCloud className="w-4 h-4" />
            <span>Upload Certificate</span>
          </button>
          {profile?.rollNumber && (
            <Link
              to={`/portfolio/${profile.rollNumber}`}
              target="_blank"
              className="inline-flex items-center space-x-1.5 px-4 py-2.5 text-sm font-semibold text-slate-700 hover:text-blue-600 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-xl transition-colors"
            >
              <span>Public Portfolio</span>
              <ExternalLink className="w-4 h-4" />
            </Link>
          )}
        </div>
      </div>

      {/* Blueprint Spec: Academic Summary Metrics (grid-cols-3) */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Metric 1: CGPA */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              Academic Standing (CGPA)
            </p>
            <h3 className="text-3xl font-extrabold text-slate-900 mt-1">
              {Number(profile?.cgpa || 0).toFixed(2)}
              <span className="text-sm font-medium text-slate-400 ml-1">/ 10.0</span>
            </h3>
            <p className="text-xs text-emerald-600 font-semibold mt-1 flex items-center">
              <CheckCircle2 className="w-3.5 h-3.5 mr-1" />
              Verified Institutional Record
            </p>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <GraduationCap className="w-6 h-6" />
          </div>
        </div>

        {/* Metric 2: Verified Count */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              Verified Certifications
            </p>
            <h3 className="text-3xl font-extrabold text-slate-900 mt-1">
              {verifiedCertCount}
              <span className="text-sm font-normal text-slate-500 ml-2">
                ({pendingCertCount} pending audit)
              </span>
            </h3>
            <p className="text-xs text-blue-600 font-semibold mt-1">
              Faculty Signed & Immutable
            </p>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <Award className="w-6 h-6" />
          </div>
        </div>

        {/* Metric 3: Skill Count */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              Total Endorsed Skills
            </p>
            <h3 className="text-3xl font-extrabold text-slate-900 mt-1">
              {skills.length}
            </h3>
            <p className="text-xs text-slate-500 font-medium mt-1">
              Categorized in Skill Matrix
            </p>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center">
            <Code2 className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Notifications */}
      {actionSuccess && (
        <div className="p-4 text-sm font-medium text-emerald-800 bg-emerald-50 border border-emerald-200 rounded-2xl animate-fade-in flex items-center space-x-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          <span>{actionSuccess}</span>
        </div>
      )}
      {actionError && (
        <div className="p-4 text-sm font-medium text-rose-800 bg-rose-50 border border-rose-200 rounded-2xl animate-fade-in flex items-center space-x-2">
          <AlertTriangle className="w-4 h-4 text-rose-600" />
          <span>{actionError}</span>
        </div>
      )}

      {/* Blueprint Spec: Interactive Skill Selector & Matrix */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left: Add New Skill */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div>
            <h3 className="text-base font-bold text-slate-900 flex items-center space-x-2">
              <Sparkles className="w-4 h-4 text-blue-600" />
              <span>Attach Skill to Profile</span>
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">
              Select master competencies to showcase on your portfolio
            </p>
          </div>

          <form onSubmit={handleAddSkill} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Select Skill Catalog
              </label>
              <select
                required
                value={selectedSkillId}
                onChange={(e) => setSelectedSkillId(e.target.value)}
                className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              >
                <option value="">-- Choose Technology / Framework --</option>
                {availableCatalogSkills.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.skillName} ({s.category})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Proficiency Pill
              </label>
              <div className="grid grid-cols-3 gap-2">
                {['BEGINNER', 'INTERMEDIATE', 'ADVANCED'].map((lvl) => (
                  <button
                    key={lvl}
                    type="button"
                    onClick={() => setSelectedProficiency(lvl)}
                    className={`py-1.5 text-xs font-bold rounded-lg border transition-all ${
                      selectedProficiency === lvl
                        ? 'bg-blue-600 text-white border-blue-600 shadow-sm'
                        : 'bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100'
                    }`}
                  >
                    {lvl}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Project / GitHub Link (Proof)
              </label>
              <input
                type="url"
                value={projectLink}
                onChange={(e) => setProjectLink(e.target.value)}
                placeholder="https://github.com/your-username/repo"
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <button
              type="submit"
              disabled={!selectedSkillId}
              className="w-full flex items-center justify-center space-x-2 py-2 px-4 rounded-xl text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-40 transition-all shadow-sm"
            >
              <Plus className="w-4 h-4" />
              <span>Attach Skill</span>
            </button>
          </form>
        </div>

        {/* Right: Active Skill Matrix Grid */}
        <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-base font-bold text-slate-900">
                Endorsed Skill Matrix ({skills.length})
              </h3>
              <p className="text-xs text-slate-500">
                Proficiency badges and verified project repositories
              </p>
            </div>
            <span className="text-xs font-medium text-slate-400">
              Interactive Badges
            </span>
          </div>

          {skills.length === 0 ? (
            <div className="p-8 text-center border-2 border-dashed border-slate-200 rounded-xl">
              <Code2 className="w-8 h-8 text-slate-400 mx-auto mb-2" />
              <p className="text-sm font-semibold text-slate-600">No skills attached yet</p>
              <p className="text-xs text-slate-400 mt-1">
                Select your tech competencies on the left to populate your registry.
              </p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {skills.map((skill) => (
                <SkillBadge
                  key={skill.id}
                  skill={skill}
                  canDelete={true}
                  onDelete={handleDeleteSkill}
                />
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Certificate Upload Modal */}
      <CertUploadModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        studentId={studentId}
        onUploadSuccess={(newCert) => {
          setCertificates([newCert, ...certificates]);
          setActionSuccess('Certificate submitted successfully! Status: PENDING faculty audit.');
          setTimeout(() => setActionSuccess(''), 5000);
        }}
      />
    </div>
  );
}
