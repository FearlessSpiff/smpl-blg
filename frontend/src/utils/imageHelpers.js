/**
 * Determines if an image is in portrait orientation
 * @param {number} width - Image width
 * @param {number} height - Image height
 * @returns {boolean} - True if portrait orientation
 */
export function isPortrait(width, height) {
  return height / width >= 1.0;
}

/**
 * Calculates the aspect ratio of an image
 * @param {number} width - Image width
 * @param {number} height - Image height
 * @returns {number} - Aspect ratio (width/height)
 */
export function aspectRatio(width, height) {
  return width / height;
}
