/* eslint-disable no-undef */
// For Jest compatibility
const config = {
    presets: [
        ['babel-preset-expo'],
        ["module:metro-react-native-babel-preset"],
    ],
    plugins: [
        ['@babel/plugin-proposal-decorators', { legacy: true }],
        // Vous pouvez ajouter d'autres plugins ici si nécessaire
    ],
};

// Export in a way that works with both ESLint and Jest
if (typeof module !== 'undefined') {
    module.exports = config;
}

export default config;