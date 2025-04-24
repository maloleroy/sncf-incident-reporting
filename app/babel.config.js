/* eslint-disable no-undef */
// For Jest compatibility
const config = {
    presets: [
        ['babel-preset-expo'],
    ],
    plugins: [
        ['@babel/plugin-proposal-decorators', { legacy: true }],
    ],
};

// Export in a way that works with both ESLint and Jest
if (typeof module !== 'undefined') {
    module.exports = config;
}

export default config;