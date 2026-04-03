// scripts/update-version.js
//
// Updater customizado para o commit-and-tag-version.
// Responsável por ler e escrever o versionName dentro do
// app/build.gradle.kts, que usa sintaxe Kotlin e não JSON.
//
// Funções obrigatórias pela interface do commit-and-tag-version:
//   • readVersion(contents)  → retorna a versão atual como string
//   • writeVersion(contents, version) → retorna o arquivo atualizado

'use strict';

// Regex robusto que encontra versionName independente de espaços
// ao redor do "=" e aceita versões no formato X.Y.Z ou X.Y.Z-sufixo
const VERSION_REGEX = /versionName\s*=\s*"([^"]+)"/;

/**
 * Lê a versão atual do conteúdo do build.gradle.kts.
 * Chamado pelo commit-and-tag-version para saber a versão de partida.
 *
 * @param {string} contents - Conteúdo completo do arquivo
 * @returns {string} - Versão atual, ex: "1.0.0"
 */
function readVersion(contents) {
  const match = contents.match(VERSION_REGEX);

  if (!match) {
    throw new Error(
      '[update-version.js] Não foi possível encontrar o versionName ' +
      'no app/build.gradle.kts. Certifique-se de que o arquivo contém ' +
      'uma linha no formato: versionName = "X.Y.Z"'
    );
  }

  return match[1];
}

/**
 * Substitui o versionName pelo novo valor gerado pelo commit-and-tag-version.
 * Chamado automaticamente após calcular a nova versão.
 *
 * @param {string} contents - Conteúdo completo do arquivo
 * @param {string} version  - Nova versão calculada, ex: "1.1.0"
 * @returns {string} - Conteúdo atualizado do arquivo
 */
function writeVersion(contents, version) {
  if (!VERSION_REGEX.test(contents)) {
    throw new Error(
      '[update-version.js] Não foi possível substituir o versionName ' +
      'no app/build.gradle.kts. Verifique se o formato está correto.'
    );
  }

  return contents.replace(
    VERSION_REGEX,
    `versionName = "${version}"`
  );
}

module.exports = { readVersion, writeVersion };