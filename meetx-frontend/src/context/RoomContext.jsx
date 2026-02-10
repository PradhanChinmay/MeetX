import React, { createContext, useContext, useState } from 'react';

const RoomContext = createContext();

export const useRoom = () => {
  const context = useContext(RoomContext);
  if (!context) {
    throw new Error('useRoom must be used within RoomProvider');
  }
  return context;
};

export const RoomProvider = ({ children }) => {
  const [roomData, setRoomData] = useState(null);
  const [participantId, setParticipantId] = useState(null);

  const updateRoomData = (data) => {
    setRoomData(data);
  };

  const updateParticipantId = (id) => {
    setParticipantId(id);
  };

  const clearRoom = () => {
    setRoomData(null);
    setParticipantId(null);
  };

  return (
    <RoomContext.Provider
      value={{
        roomData,
        participantId,
        updateRoomData,
        updateParticipantId,
        clearRoom
      }}
    >
      {children}
    </RoomContext.Provider>
  );
};