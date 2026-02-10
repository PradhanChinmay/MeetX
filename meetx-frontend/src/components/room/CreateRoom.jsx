import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../common/Card';
import Input from '../common/Input';
import Button from '../common/Button';
import { validateRoomName } from '../../utils/validators';
import { roomAPI } from '../../services/api';
import { useRoom } from '../../context/RoomContext';
import './CreateRoom.css';

const CreateRoom = () => {
  const [roomName, setRoomName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { updateRoomData } = useRoom();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const validationError = validateRoomName(roomName);
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await roomAPI.createRoom(roomName);
      updateRoomData(response);
      navigate(`/room/${response.roomId}?token=${response.token}`);
    } catch (err) {
      setError(err.message || 'Failed to create room');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="Create New Room" className="create-room-card">
      <form onSubmit={handleSubmit}>
        <Input
          label="Room Name"
          value={roomName}
          onChange={(e) => setRoomName(e.target.value)}
          placeholder="Enter room name"
          error={error}
          required
          disabled={loading}
        />
        <Button 
          type="submit" 
          variant="primary" 
          fullWidth
          loading={loading}
        >
          Create Room
        </Button>
      </form>
    </Card>
  );
};

export default CreateRoom;