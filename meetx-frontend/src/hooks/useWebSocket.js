import { useState, useCallback, useRef } from 'react';

export const useWebSocket = () => {
  const [isConnected, setIsConnected] = useState(false);
  const socketRef = useRef(null);
  const callbacksRef = useRef({});

  const connect = useCallback((url) => {
    return new Promise((resolve, reject) => {
      if (socketRef.current?.readyState === WebSocket.OPEN) return resolve();
      
      socketRef.current = new WebSocket(url);
      socketRef.current.onopen = () => { setIsConnected(true); resolve(); };
      socketRef.current.onclose = () => { setIsConnected(false); };
      socketRef.current.onerror = (error) => reject(error);

      socketRef.current.onmessage = (event) => {
        const message = JSON.parse(event.data);
        
        // --- THIS LOG IS THE KEY ---
        console.log("%c[WS INCOMING]", "color: cyan; font-weight: bold", message.type, message);

        const handler = callbacksRef.current[message.type];
        if (handler) {
          handler(message);
        } else {
          console.warn(`[WS] No handler registered for type: ${message.type}`);
        }
      };
    });
  }, []);

  const disconnect = useCallback(() => {
    if (socketRef.current) { socketRef.current.close(); socketRef.current = null; setIsConnected(false); }
  }, []);

  const sendMessage = useCallback((message) => {
    if (socketRef.current?.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify(message));
    }
  }, []);

  const on = useCallback((type, callback) => { callbacksRef.current[type] = callback; }, []);
  const off = useCallback((type) => { delete callbacksRef.current[type]; }, []);

  return { isConnected, connect, disconnect, sendMessage, on, off };
};