import { useState, useEffect, useCallback } from 'react';
import { MEDIA_CONSTRAINTS, ERROR_MESSAGES } from '../utils/constants';

export const useMediaStream = () => {
  const [localStream, setLocalStream] = useState(null);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isMuted, setIsMuted] = useState(false);
  const [isVideoOff, setIsVideoOff] = useState(false);

  const getMediaStream = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      const stream = await navigator.mediaDevices.getUserMedia(MEDIA_CONSTRAINTS);
      setLocalStream(stream);
      return stream;
    } catch (err) {
      let errorMessage = ERROR_MESSAGES.CAMERA_ACCESS_DENIED;
      
      if (err.name === 'NotFoundError') {
        errorMessage = ERROR_MESSAGES.CAMERA_NOT_FOUND;
      } else if (err.name === 'NotAllowedError') {
        errorMessage = ERROR_MESSAGES.CAMERA_ACCESS_DENIED;
      }
      
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const stopMediaStream = useCallback(() => {
    if (localStream) {
      localStream.getTracks().forEach(track => track.stop());
      setLocalStream(null);
    }
  }, [localStream]);

  const toggleAudio = useCallback(() => {
    if (localStream) {
      const audioTrack = localStream.getAudioTracks()[0];
      if (audioTrack) {
        audioTrack.enabled = !audioTrack.enabled;
        setIsMuted(!audioTrack.enabled);
      }
    }
  }, [localStream]);

  const toggleVideo = useCallback(() => {
    if (localStream) {
      const videoTrack = localStream.getVideoTracks()[0];
      if (videoTrack) {
        videoTrack.enabled = !videoTrack.enabled;
        setIsVideoOff(!videoTrack.enabled);
      }
    }
  }, [localStream]);

  useEffect(() => {
    return () => {
      stopMediaStream();
    };
  }, [stopMediaStream]);

  return {
    localStream,
    error,
    isLoading,
    isMuted,
    isVideoOff,
    getMediaStream,
    stopMediaStream,
    toggleAudio,
    toggleVideo
  };
};