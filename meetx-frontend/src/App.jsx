import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/layout/Layout';
import ErrorBoundary from './components/common/ErrorBoundary';
import { RoomProvider } from './context/RoomContext';
import Home from './pages/Home';
import Room from './pages/Room';
import './App.css';

function App() {
  return (
    <ErrorBoundary>
      <RoomProvider>
        <Router>
          <Layout>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/room/:roomId" element={<Room />} />
            </Routes>
          </Layout>
        </Router>
      </RoomProvider>
    </ErrorBoundary>
  );
}

export default App;