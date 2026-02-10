import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../common/Card';
import Input from '../common/Input';
import Button from '../common/Button';
import { validateRoomId } from '../../utils/validators';
import { roomAPI } from '../../services/api';
import { useRoom } from '../../context/RoomContext';
import './JoinRoom.css';

const JoinRoom = () => {
  const [roomId, setRoomId] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { updateRoomData } = useRoom();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const validationError = validateRoomId(roomId);
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await roomAPI.joinRoom(roomId.trim());
      updateRoomData(response);
      navigate(`/room/${response.roomId}?token=${response.token}`);
    } catch (err) {
      setError(err.message || 'Failed to join room');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="Join Existing Room" className="join-room-card">
      <form onSubmit={handleSubmit}>
        <Input
          label="Room ID"
          value={roomId}
          onChange={(e) => setRoomId(e.target.value)}
          placeholder="Enter room ID"
          error={error}
          required
          disabled={loading}
        />
        <Button 
          type="submit" 
          variant="success" 
          fullWidth
          loading={loading}
        >
          Join Room
        </Button>
      </form>
    </Card>
  );
};

export default JoinRoom;