import React from 'react';
import CreateRoom from '../components/room/CreateRoom';
import JoinRoom from '../components/room/JoinRoom';
import './Home.css';

const Home = () => {
  return (
    <div className="home">
      <div className="home-hero">
        <h1 className="home-title">Welcome to MeetX</h1>
        <p className="home-subtitle">
          Connect face-to-face with crystal-clear video calling
        </p>
      </div>

      <div className="home-actions">
        <CreateRoom />
        <div className="divider">
          <span>OR</span>
        </div>
        <JoinRoom />
      </div>

      <div className="home-features">
        <div className="feature">
          <div className="feature-icon">🎥</div>
          <h3>HD Video Quality</h3>
          <p>Crystal clear video with adaptive quality</p>
        </div>
        <div className="feature">
          <div className="feature-icon">🔒</div>
          <h3>Secure & Private</h3>
          <p>End-to-end encrypted connections</p>
        </div>
        <div className="feature">
          <div className="feature-icon">⚡</div>
          <h3>Real-time Connection</h3>
          <p>Low latency peer-to-peer technology</p>
        </div>
      </div>
    </div>
  );
};

export default Home;