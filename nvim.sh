#!/usr/bin/env bash
set -euo pipefail

APPNAME="nvim_preso"
CFG_DIR="$HOME/.config/${APPNAME}"
PKG_DIR="${CFG_DIR}/pack/plugins/start"
INIT="${CFG_DIR}/init.lua"
ORIG_CWD="$(pwd -P)"

mkdir -p "${PKG_DIR}"

# ---------------- init.lua ----------------
cat > "${INIT}" <<'LUA'
-- Markdown fenced code
vim.g.markdown_fenced_languages = { "java" }

-- Look
vim.o.background = "light"
vim.o.termguicolors = true
vim.cmd("syntax on")
vim.cmd("colorscheme lunaperche")

-- Treesitter (safe if missing)
pcall(function()
  require("nvim-treesitter.configs").setup({
    highlight = { enable = true },
  })
end)

-- Markview (safe if missing)
vim.schedule(function()
  pcall(function()
    require("markview").setup({})
  end)
end)

-- vim-slime -> tmux last accessed pane
vim.g.slime_target = "tmux"
vim.g.slime_dont_ask_default = 1
vim.g.slime_bracketed_paste = 1
vim.g.slime_default_config = { socket_name = "default", target_pane = "{last}" }
LUA
# ------------------------------------------

# Plugins (native pack/*). Clone only if missing.
[ -d "${PKG_DIR}/nvim-treesitter/.git" ] || git clone https://github.com/nvim-treesitter/nvim-treesitter.git "${PKG_DIR}/nvim-treesitter"
[ -d "${PKG_DIR}/markview.nvim/.git" ]   || git clone https://github.com/OXY2DEV/markview.nvim.git           "${PKG_DIR}/markview.nvim"
[ -d "${PKG_DIR}/vim-slime/.git" ]       || git clone https://github.com/jpalardy/vim-slime.git               "${PKG_DIR}/vim-slime"

# Treesitter parsers without prompts:
# First try update (never asks). If that fails, force install without confirmation.
if ! NVIM_APPNAME="${APPNAME}" nvim --headless "+TSUpdateSync markdown markdown_inline java" +qa; then
  NVIM_APPNAME="${APPNAME}" nvim --headless "+TSInstallSync! markdown markdown_inline java" +qa || true
fi

# Launch Neovim from the original directory; pass through user args.
NVIM_APPNAME="${APPNAME}" nvim -c "cd ${ORIG_CWD}" "$@"

