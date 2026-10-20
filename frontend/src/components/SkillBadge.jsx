import React from 'react';
import { CheckCircle, ExternalLink, Trash2 } from 'lucide-react';

export default function SkillBadge({ skill, onDelete, canDelete = false }) {
  const getProficiencyStyle = (level) => {
    switch (level?.toUpperCase()) {
      case 'ADVANCED':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200 ring-emerald-500/20';
      case 'INTERMEDIATE':
        return 'bg-blue-50 text-blue-700 border-blue-200 ring-blue-500/20';
      case 'BEGINNER':
      default:
        return 'bg-slate-100 text-slate-700 border-slate-200 ring-slate-500/20';
    }
  };

  const getCategoryColor = (cat) => {
    switch (cat?.toUpperCase()) {
      case 'BACKEND': return 'bg-amber-100 text-amber-800';
      case 'FRONTEND': return 'bg-indigo-100 text-indigo-800';
      case 'DATABASE': return 'bg-purple-100 text-purple-800';
      case 'CLOUD': return 'bg-cyan-100 text-cyan-800';
      case 'TOOLS': return 'bg-orange-100 text-orange-800';
      default: return 'bg-slate-100 text-slate-800';
    }
  };

  return (
    <div className="group relative flex items-center justify-between p-3 bg-white rounded-xl border border-slate-200 hover:border-blue-300 shadow-sm hover:shadow-md transition-all">
      <div className="flex flex-col space-y-1">
        <div className="flex items-center space-x-2">
          <span className="font-semibold text-slate-800 text-sm">{skill.skillName}</span>
          {skill.endorsed && (
            <span title="Faculty Endorsed Skill" className="inline-flex items-center text-blue-600">
              <CheckCircle className="w-3.5 h-3.5 fill-blue-50" />
            </span>
          )}
        </div>
        <div className="flex items-center space-x-2">
          <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full border ${getProficiencyStyle(skill.proficiency)}`}>
            {skill.proficiency}
          </span>
          {skill.category && (
            <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${getCategoryColor(skill.category)}`}>
              {skill.category}
            </span>
          )}
        </div>
      </div>

      <div className="flex items-center space-x-2">
        {skill.projectLink && (
          <a
            href={skill.projectLink}
            target="_blank"
            rel="noopener noreferrer"
            title="View proof project"
            className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
          >
            <ExternalLink className="w-3.5 h-3.5" />
          </a>
        )}

        {canDelete && onDelete && (
          <button
            onClick={() => onDelete(skill.id)}
            title="Remove skill"
            className="p-1.5 text-slate-300 hover:text-rose-600 hover:bg-rose-50 rounded-lg opacity-0 group-hover:opacity-100 transition-all"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        )}
      </div>
    </div>
  );
}
