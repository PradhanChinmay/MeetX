export class WebRTCService {
  constructor(iceServers) {
    this.peerConnection = null;
    this.iceServers = iceServers || [
      { urls: 'stun:stun.l.google.com:19302' },
      { urls: 'stun:stun1.l.google.com:19302' }
    ];
  }

  createPeerConnection(callbacks = {}) {
    const config = {
      iceServers: this.iceServers
    };

    this.peerConnection = new RTCPeerConnection(config);

    // ICE candidate handler
    this.peerConnection.onicecandidate = (event) => {
      if (event.candidate && callbacks.onIceCandidate) {
        callbacks.onIceCandidate(event.candidate);
      }
    };

    // Track handler (receive remote stream)
    this.peerConnection.ontrack = (event) => {
      if (callbacks.onTrack) {
        callbacks.onTrack(event.streams[0]);
      }
    };

    // Connection state change handler
    this.peerConnection.onconnectionstatechange = () => {
      if (callbacks.onConnectionStateChange) {
        callbacks.onConnectionStateChange(this.peerConnection.connectionState);
      }
    };

    // ICE connection state change handler
    this.peerConnection.oniceconnectionstatechange = () => {
      if (callbacks.onIceConnectionStateChange) {
        callbacks.onIceConnectionStateChange(this.peerConnection.iceConnectionState);
      }
    };

    return this.peerConnection;
  }

  async addLocalStream(stream) {
    if (!this.peerConnection) {
      throw new Error('Peer connection not initialized');
    }

    stream.getTracks().forEach(track => {
      this.peerConnection.addTrack(track, stream);
    });
  }

  async createOffer() {
    if (!this.peerConnection) {
      throw new Error('Peer connection not initialized');
    }

    const offer = await this.peerConnection.createOffer();
    await this.peerConnection.setLocalDescription(offer);
    return offer;
  }

  async createAnswer() {
    if (!this.peerConnection) {
      throw new Error('Peer connection not initialized');
    }

    const answer = await this.peerConnection.createAnswer();
    await this.peerConnection.setLocalDescription(answer);
    return answer;
  }

  async handleOffer(offer) {
    if (!this.peerConnection) {
      throw new Error('Peer connection not initialized');
    }

    await this.peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
  }

  async handleAnswer(answer) {
    if (!this.peerConnection) {
      throw new Error('Peer connection not initialized');
    }

    await this.peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
  }

  async addIceCandidate(candidate) {
    if (!this.peerConnection) {
      throw new Error('Peer connection not initialized');
    }

    try {
      await this.peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    } catch (error) {
      console.error('Error adding ICE candidate:', error);
    }
  }

  close() {
    if (this.peerConnection) {
      this.peerConnection.close();
      this.peerConnection = null;
    }
  }

  getConnectionState() {
    return this.peerConnection ? this.peerConnection.connectionState : 'closed';
  }

  getIceConnectionState() {
    return this.peerConnection ? this.peerConnection.iceConnectionState : 'closed';
  }
}

export default WebRTCService;