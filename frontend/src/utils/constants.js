/**
 * Display size multipliers for responsive image sizing
 */

/** Mobile device width multiplier (80% of viewport width) */
export const MOBILE_WIDTH_MULTIPLIER = 0.8;

/** Desktop device width multiplier (75% of viewport width) */
export const DESKTOP_WIDTH_MULTIPLIER = 0.75;

/** Desktop device height multiplier (80% of viewport height) */
export const DESKTOP_HEIGHT_MULTIPLIER = 0.8;

/**
 * Polaroid card widths by Vuetify breakpoint
 * Null values indicate percentage-based sizing should be used
 * @type {Object.<string, number|null>}
 */
export const POLAROID_WIDTH = {
  XS: null, // Use percentage
  SM: null, // Use percentage
  MD: 400,
  LG: 500,
  XL: 500,
  XXL: 500,
  DEFAULT: 280
};

/** Maximum rotation angle in degrees for polaroid tilt effect */
export const POLAROID_ROTATION_DEGREES = 5;
