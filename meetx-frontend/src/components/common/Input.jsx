import React from 'react';
import './Input.css';

const Input = ({ 
  label, 
  error, 
  type = 'text',
  value,
  onChange,
  placeholder,
  required = false,
  disabled = false,
  className = ''
}) => {
  return (
    <div className={`input-group ${className}`}>
      {label && (
        <label className="input-label">
          {label}
          {required && <span className="required">*</span>}
        </label>
      )}
      <input
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        disabled={disabled}
        className={`input ${error ? 'input-error' : ''}`}
      />
      {error && <span className="error-message">{error}</span>}
    </div>
  );
};

export default Input;