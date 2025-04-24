module.exports = {
    presets: [
        ['babel-preset-expo'],
        ["module:metro-react-native-babel-preset"],
    ],
    plugins: [
        ['@babel/plugin-proposal-decorators', { legacy: true }],
        // Vous pouvez ajouter d'autres plugins ici si nécessaire
    ],
};