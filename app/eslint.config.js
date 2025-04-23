import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';
import reactPlugin from 'eslint-plugin-react';
import reactHooksPlugin from 'eslint-plugin-react-hooks';
import reactNativePlugin from 'eslint-plugin-react-native';
import globals from 'globals';

export default tseslint.config(
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  {
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
      globals: {
        ...globals.node,
        ...globals.browser,
        console: true,
        require: true,
        module: true,
        process: true,
        __dirname: true,
        __filename: true,
      },
    },
    plugins: {
      'react': reactPlugin,
      'react-hooks': reactHooksPlugin,
      'react-native': reactNativePlugin,
    },
    rules: {
      'react/react-in-jsx-scope': 'off',
      'react/prop-types': 'off',
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-explicit-any': 'warn',
      '@typescript-eslint/no-unused-vars': ['warn', { 
        'argsIgnorePattern': '^_',
        'varsIgnorePattern': '^_',
        'caughtErrorsIgnorePattern': '^_'
      }],
      '@typescript-eslint/no-unused-expressions': 'off',
      'no-case-declarations': 'off',
      'no-undef': 'off',
      'no-empty': 'warn',
      '@typescript-eslint/no-require-imports': 'off',
      'no-fallthrough': 'warn',
      'no-constant-binary-expression': 'error',
      'no-cond-assign': 'warn',
      'no-control-regex': 'warn',
      'no-extra-boolean-cast': 'warn',
      'no-unreachable': 'warn',
    },
    ignores: [
      '**/node_modules/**',
      '**/dist/**', 
      '**/build/**',
      '**/lib/**',
      '**/scripts/**',
      '.expo/**',
      '**/*.config.js',
      '**/metro.config.js',
      '**/babel.config.js',
      '**/*.generated.*',
      '**/*.d.ts',
      '**/*.test.*',
      '**/*.spec.*',
      '**/*.md',
      'coverage/**',
      '.vercel/**',
      '.next/**',
      '.vscode/**',
      '.idea/**',
      'package-lock.json',
      'yarn.lock',
      'pnpm-lock.yaml'
    ],
  }
); 