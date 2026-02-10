import React from 'react';
import './Header.css';

const Header = () => {
  return (
    <header className="header">
      <div className="header-content">
        <div className="logo">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <circle cx="16" cy="16" r="14" fill="#667eea"/>
            <path d="M12 12L20 16L12 20V12Z" fill="white"/>
          </svg>
          <h1>VideoTranslate</h1>
        </div>
        <div className="header-subtitle">Phase 1: Core WebRTC Platform</div>
      </div>
    </header>
  );
};

export default Header;