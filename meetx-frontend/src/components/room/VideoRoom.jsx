import React, { useEffect, useState, useCallback, useRef, useMemo } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import VideoPlayer from "./VideoPlayer";
import Controls from "./Controls";
import LoadingSpinner from "../common/LoadingSpinner";
import { useMediaStream } from "../../hooks/useMediaStream";
import { useWebSocket } from "../../hooks/useWebSocket";
import { useWebRTC } from "../../hooks/useWebRTC";
import { useRoom } from "../../context/RoomContext";
import { MESSAGE_TYPES } from "../../utils/constants";
import "./VideoRoom.css";

const WS_URL = import.meta.env.VITE_WS_URL || "ws://localhost:8080";

const VideoRoom = () => {
  const { roomId } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { roomData, participantId, updateParticipantId, clearRoom } = useRoom();
  const [error, setError] = useState(null);
  const [isInitialized, setIsInitialized] = useState(false);
  const [remoteParticipantId, setRemoteParticipantId] = useState(null);

  const participantIdRef = useRef(null);
  useEffect(() => { participantIdRef.current = participantId; }, [participantId]);

  const { localStream, getMediaStream, stopMediaStream, isMuted, isVideoOff, toggleAudio, toggleVideo, isLoading: isMediaLoading } = useMediaStream();
  const { isConnected: wsConnected, connect: wsConnect, disconnect: wsDisconnect, sendMessage, on, off } = useWebSocket();
  const wsService = useMemo(() => ({ sendMessage, isConnected: wsConnected }), [sendMessage, wsConnected]);

  const { remoteStream, connectionState, createOffer, handleOffer, handleAnswer, handleIceCandidate, closeConnection } = useWebRTC(
    wsService, localStream, roomData?.iceServers
  );

  useEffect(() => {
    const init = async () => {
      try {
        await getMediaStream();
        const token = searchParams.get("token");
        await wsConnect(`${WS_URL}/ws/signaling`);
        sendMessage({ type: MESSAGE_TYPES.JOIN, roomId, token });
      } catch (err) { setError(err.message); }
    };
    init();
    return () => { stopMediaStream(); wsDisconnect(); closeConnection(); clearRoom(); };
  }, []);

  useEffect(() => {
    if (!wsConnected) return;

    on(MESSAGE_TYPES.READY, (msg) => {
      updateParticipantId(msg.participantId);
      setIsInitialized(true);
    });

    on(MESSAGE_TYPES.PARTICIPANT_JOINED, async (msg) => {
      console.log("%c[VideoRoom] Action: Initiating Offer", "color: green");
      if (msg.participantId !== participantIdRef.current) {
        setRemoteParticipantId(msg.participantId);
        await createOffer(msg.participantId);
      }
    });

    on(MESSAGE_TYPES.OFFER, async (msg) => {
      setRemoteParticipantId(msg.participantId);
      await handleOffer(msg.payload, msg.participantId);
    });

    on(MESSAGE_TYPES.ANSWER, async (msg) => { await handleAnswer(msg.payload); });
    on(MESSAGE_TYPES.ICE_CANDIDATE, async (msg) => { await handleIceCandidate(msg.payload); });

    return () => { Object.values(MESSAGE_TYPES).forEach(type => off(type)); };
  }, [wsConnected, on, off, createOffer, handleOffer, handleAnswer, handleIceCandidate]);

  if (isMediaLoading || !isInitialized) return <LoadingSpinner message="Setting up video room..." />;

  return (
    <div className="video-room">
      <div className="video-room-header">
        <h2>{roomData?.roomName || "Video Room"}</h2>
        <div className="room-info">
          <span>Room: {roomId}</span>
          {remoteParticipantId && <span className="participant-count">• 2 Participants</span>}
        </div>
      </div>
      <div className="video-grid">
        <div className="video-container">
          <VideoPlayer stream={remoteStream} label="Remote Participant" isLocal={false} />
        </div>
        <div className="video-container video-container-small">
          <VideoPlayer stream={localStream} muted={true} label="You" isLocal={true} />
        </div>
      </div>
      <Controls 
        isMuted={isMuted} isVideoOff={isVideoOff} 
        onToggleAudio={toggleAudio} onToggleVideo={toggleVideo} 
        connectionState={connectionState} onLeaveRoom={() => navigate("/")} 
      />
    </div>
  );
};

export default VideoRoom;