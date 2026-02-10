import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));

// Note: StrictMode causes double mounting in development which can cause
// multiple WebSocket connections. For WebRTC apps, we disable it.
// In production builds, this doesn't matter as StrictMode is automatically disabled.

const isDevelopment = import.meta.env.DEV;

root.render(
  isDevelopment ? (
    <App />
  ) : (
    <React.StrictMode>
      <App />
    </React.StrictMode>
  )
);