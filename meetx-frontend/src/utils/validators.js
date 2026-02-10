export const validateRoomName = (name) => {
  if (!name || name.trim().length === 0) {
    return 'Room name is required';
  }
  if (name.trim().length < 3) {
    return 'Room name must be at least 3 characters';
  }
  if (name.trim().length > 50) {
    return 'Room name must not exceed 50 characters';
  }
  return null;
};

export const validateRoomId = (roomId) => {
  if (!roomId || roomId.trim().length === 0) {
    return 'Room ID is required';
  }
  return null;
};