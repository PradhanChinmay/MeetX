import React from 'react';
import './Button.css';

const Button = ({ 
  children, 
  onClick, 
  variant = 'primary', 
  disabled = false,
  loading = false,
  type = 'button',
  fullWidth = false,
  className = ''
}) => {
  return (
    <button
      type={type}
      className={`btn btn-${variant} ${fullWidth ? 'btn-full-width' : ''} ${className}`}
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading ? (
        <span className="btn-loading">
          <span className="spinner-small"></span>
          Loading...
        </span>
      ) : children}
    </button>
  );
};

export default Button;

