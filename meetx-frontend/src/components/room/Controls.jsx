import React from 'react';
import Button from '../common/Button';
import './Controls.css';

const Controls = ({ 
  isMuted, 
  isVideoOff, 
  onToggleAudio, 
  onToggleVideo, 
  onLeaveRoom,
  connectionState 
}) => {
  return (
    <div className="controls">
      <div className="controls-left">
        <div className={`connection-status status-${connectionState}`}>
          <span className="status-dot"></span>
          {connectionState === 'connected' ? 'Connected' : 
           connectionState === 'connecting' ? 'Connecting...' : 
           'Disconnected'}
        </div>
      </div>
      
      <div className="controls-center">
        <button
          onClick={onToggleAudio}
          className={`control-btn ${isMuted ? 'control-btn-danger' : ''}`}
          title={isMuted ? 'Unmute' : 'Mute'}
        >
          {isMuted ? (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="1" y1="1" x2="23" y2="23"/>
              <path d="M9 9v3a3 3 0 0 0 5.12 2.12M15 9.34V4a3 3 0 0 0-5.94-.6"/>
              <path d="M17 16.95A7 7 0 0 1 5 12v-2m14 0v2a7 7 0 0 1-.11 1.23"/>
              <line x1="12" y1="19" x2="12" y2="23"/>
              <line x1="8" y1="23" x2="16" y2="23"/>
            </svg>
          ) : (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"/>
              <path d="M19 10v2a7 7 0 0 1-14 0v-2"/>
              <line x1="12" y1="19" x2="12" y2="23"/>
              <line x1="8" y1="23" x2="16" y2="23"/>
            </svg>
          )}
        </button>

        <button
          onClick={onToggleVideo}
          className={`control-btn ${isVideoOff ? 'control-btn-danger' : ''}`}
          title={isVideoOff ? 'Turn on camera' : 'Turn off camera'}
        >
          {isVideoOff ? (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M16 16v1a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h2m5.66 0H14a2 2 0 0 1 2 2v3.34l1 1L23 7v10"/>
              <line x1="1" y1="1" x2="23" y2="23"/>
            </svg>
          ) : (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polygon points="23 7 16 12 23 17 23 7"/>
              <rect x="1" y="5" width="15" height="14" rx="2" ry="2"/>
            </svg>
          )}
        </button>

        <button
          onClick={onLeaveRoom}
          className="control-btn control-btn-leave"
          title="Leave room"
        >
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M18.36 6.64a9 9 0 1 1-12.73 0"/>
            <line x1="12" y1="2" x2="12" y2="12"/>
          </svg>
        </button>
      </div>
      
      <div className="controls-right"></div>
    </div>
  );
};

export default Controls;