# MeetX: Real-Time Language Translation Video Platform

**MeetX** is a next-generation WebRTC video conferencing platform designed to eliminate language barriers. By orchestrating a high-performance pipeline of Automatic Speech Recognition (ASR), Neural Machine Translation (NMT), and Text-to-Speech (TTS), MeetX provides live translated captions and dubbed audio streams in near real-time.

---

## 🚀 Key Features

* **Low-Latency Video:** 1:1 and multi-party video calling via WebRTC.
* **Live Multilingual Captions:** Real-time speech-to-text with interim "flicker-free" updates.
* **Voice-to-Voice Translation:** AI-generated dubbed audio injected into the call.
* **Intelligent Audio Routing:** Ability to duck original audio or toggle between source and translated streams.
* **Context-Aware NMT:** Maintains a context buffer for coherent translations of long utterances.

---

## 🛠️ Tech Stack

### **Core Infrastructure**
* **Backend:** Spring Boot (Java 17/21)
* **Signaling:** WebSockets (STOMP)
* **Frontend:** React.js, Tailwind CSS
* **Networking:** WebRTC API, Coturn (STUN/TURN)

### **AI/ML Pipeline**
* **ASR (Speech-to-Text):** Whisper.cpp / Vosk
* **NMT (Translation):** MarianNMT / Hugging Face Transformers
* **TTS (Voice Synthesis):** Coqui TTS / Piper
* **Inter-Service Comm:** gRPC & Protobuf (for low-latency inference)

---

## 🏗️ System Architecture



1.  **Capture:** Client captures audio via `getUserMedia`.
2.  **Stream:** Audio chunks are sent via WebSocket to the Spring Boot backend.
3.  **Process:** * **ASR** converts audio to text.
    * **NMT** translates text to the target language.
    * **TTS** generates the translated voice.
4.  **Sync:** The system aligns text and audio timestamps to ensure captions and voice-overs stay in sync with the speaker's rhythm.

---

## 🗺️ Development Roadmap

### **Phase 1: Foundation (Core Signaling)** 🏗️
- [x] Initial Repository Setup.
- [ ] Spring Boot WebSocket signaling (Offer/Answer/ICE).
- [ ] 1:1 WebRTC video tiles in React.
- [ ] Integrated STUN/TURN configuration.

### **Phase 2: Live Captions (ASR Pipeline)** 🎙️
- [ ] Independent ASR microservice integration.
- [ ] Client-side Voice Activity Detection (VAD) to reduce noise.
- [ ] Real-time caption UI with interim and final results.

### **Phase 3: Text Translation (NMT)** 🌍
- [ ] Translator service integration (MarianNMT/Hugging Face).
- [ ] Language selection UI and auto-detection.
- [ ] Context buffering for coherent sentence structure.

### **Phase 4: Voice-to-Voice (TTS & Routing)** 🔊
- [ ] Streaming TTS engine integration (Piper/Coqui).
- [ ] Server-side audio injection (SFU-ready).
- [ ] Audio "ducking" controls for the original speaker.

---

## 🚦 Getting Started

### Prerequisites
* Java 17+
* Node.js 18+
* Docker (for Coturn and AI services)

### Installation

1.  **Clone the repo:**
    ```bash
    git clone [https://github.com/PradhanChinmay/MeetX.git](https://github.com/PradhanChinmay/MeetX.git)
    cd MeetX
    ```

2.  **Setup Backend:**
    ```bash
    cd backend
    ./mvnw spring-boot:run
    ```

3.  **Setup Frontend:**
    ```bash
    cd frontend
    npm install
    npm start
    ```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Author:** [PradhanChinmay](https://github.com/PradhanChinmay)
