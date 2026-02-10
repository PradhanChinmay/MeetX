import React, { useEffect, useRef } from 'react';
import './VideoPlayer.css';

const VideoPlayer = ({ stream, muted = false, label, isLocal = false }) => {
  const videoRef = useRef(null);

  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;
    }
  }, [stream]);

  return (
    <div className={`video-player ${isLocal ? 'video-local' : 'video-remote'}`}>
      <video
        ref={videoRef}
        autoPlay
        playsInline
        muted={muted}
        className="video-element"
      />
      {label && <div className="video-label">{label}</div>}
      {!stream && (
        <div className="video-placeholder">
          <div className="placeholder-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M23 7l-7 5 7 5V7z"/>
              <rect x="1" y="5" width="15" height="14" rx="2" ry="2"/>
            </svg>
          </div>
          <p>{isLocal ? 'Waiting for camera...' : 'Waiting for participant...'}</p>
        </div>
      )}
    </div>
  );
};

export default VideoPlayer;