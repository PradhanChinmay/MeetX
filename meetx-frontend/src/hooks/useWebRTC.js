import { useEffect, useRef, useCallback, useState } from 'react';

export const useWebRTC = (wsService, localStream, iceServers) => {
  const [remoteStream, setRemoteStream] = useState(null);
  const [connectionState, setConnectionState] = useState('disconnected');
  
  const pcRef = useRef(null);
  const remoteParticipantIdRef = useRef(null);
  const wsServiceRef = useRef(wsService);

  // Keep wsService updated so handlers always have the latest sendMessage
  useEffect(() => {
    wsServiceRef.current = wsService;
  }, [wsService]);

  const closeConnection = useCallback(() => {
    if (pcRef.current) {
      pcRef.current.close();
      pcRef.current = null;
    }
    setRemoteStream(null);
    setConnectionState('disconnected');
    remoteParticipantIdRef.current = null;
  }, []);

  const initializePeerConnection = useCallback(() => {
    if (pcRef.current) return pcRef.current;

    const pc = new RTCPeerConnection({ iceServers: iceServers || [] });

    pc.onicecandidate = (event) => {
      if (event.candidate && remoteParticipantIdRef.current && wsServiceRef.current.isConnected) {
        wsServiceRef.current.sendMessage({
          type: 'ICE_CANDIDATE',
          targetParticipantId: remoteParticipantIdRef.current,
          payload: event.candidate
        });
      }
    };

    pc.ontrack = (event) => {
      if (event.streams && event.streams[0]) {
        setRemoteStream(event.streams[0]);
      }
    };

    pc.onconnectionstatechange = () => {
      setConnectionState(pc.connectionState);
    };

    if (localStream) {
      localStream.getTracks().forEach(track => pc.addTrack(track, localStream));
    }

    pcRef.current = pc;
    return pc;
  }, [localStream, iceServers]);

  const createOffer = async (targetId) => {
    remoteParticipantIdRef.current = targetId;
    const pc = initializePeerConnection();
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);
    
    wsServiceRef.current.sendMessage({
      type: 'OFFER',
      targetParticipantId: targetId,
      payload: offer
    });
  };

  const handleOffer = async (offer, senderId) => {
    remoteParticipantIdRef.current = senderId;
    const pc = initializePeerConnection();
    await pc.setRemoteDescription(new RTCSessionDescription(offer));
    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);
    
    wsServiceRef.current.sendMessage({
      type: 'ANSWER',
      targetParticipantId: senderId,
      payload: answer
    });
  };

  const handleAnswer = async (answer) => {
    if (pcRef.current) {
      await pcRef.current.setRemoteDescription(new RTCSessionDescription(answer));
    }
  };

  const handleIceCandidate = async (candidate) => {
    if (pcRef.current) {
      await pcRef.current.addIceCandidate(new RTCIceCandidate(candidate));
    }
  };

  return { remoteStream, connectionState, createOffer, handleOffer, handleAnswer, handleIceCandidate, closeConnection };
};