export const MESSAGE_TYPES = {
  OFFER: 'OFFER',
  ANSWER: 'ANSWER',
  ICE_CANDIDATE: 'ICE_CANDIDATE',
  JOIN: 'join',
  LEAVE: 'leave',
  ERROR: 'error',
  PARTICIPANT_JOINED: 'participant_joined',
  PARTICIPANT_LEFT: 'participant_left',
  READY: 'ready'
};

export const CONNECTION_STATES = {
  DISCONNECTED: 'disconnected',
  CONNECTING: 'connecting',
  CONNECTED: 'connected',
  FAILED: 'failed'
};

export const MEDIA_CONSTRAINTS = {
  audio: true,
  video: {
    width: { ideal: 1280 },
    height: { ideal: 720 },
    facingMode: 'user'
  }
};

export const ICE_CONNECTION_STATES = {
  NEW: 'new',
  CHECKING: 'checking',
  CONNECTED: 'connected',
  COMPLETED: 'completed',
  FAILED: 'failed',
  DISCONNECTED: 'disconnected',
  CLOSED: 'closed'
};

export const ERROR_MESSAGES = {
  CAMERA_ACCESS_DENIED: 'Camera/microphone access denied. Please allow permissions.',
  CAMERA_NOT_FOUND: 'No camera or microphone found.',
  CONNECTION_FAILED: 'Failed to connect to the server.',
  PEER_CONNECTION_FAILED: 'Failed to establish peer connection.',
  ROOM_NOT_FOUND: 'Room not found.',
  ROOM_FULL: 'Room is full.',
  INVALID_TOKEN: 'Invalid or expired token.',
  NETWORK_ERROR: 'Network error. Please check your connection.'
};