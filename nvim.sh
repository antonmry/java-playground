#!/usr/bin/env bash
set -euo pipefail

APPNAME="nvim_preso"
CFG_DIR="$HOME/.config/${APPNAME}"
PKG_DIR="${CFG_DIR}/pack/plugins/start"
INIT="${CFG_DIR}/init.lua"
QUERY_DIR="${CFG_DIR}/after/queries/markdown"
ORIG_CWD="$(pwd -P)"

mkdir -p "${PKG_DIR}" "${QUERY_DIR}"

# ---------------- init.lua ----------------
cat > "${INIT}" <<'LUA'
-- Markdown fenced code
vim.g.markdown_fenced_languages = { "java" }

-- Look
vim.o.background = "light"
vim.o.termguicolors = true
vim.cmd("syntax on")
vim.cmd("colorscheme lunaperche")

-- Treesitter + textobjects (safe if missing)
pcall(function()
  require("nvim-treesitter.configs").setup({
    highlight = { enable = true },
    textobjects = {
      select = {
        enable = true,
        lookahead = true,
        keymaps = {
          ["ac"] = "@code_block.outer",
          ["ic"] = "@code_block.inner",
        },
      },
    },
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

# Treesitter textobjects query for Markdown
cat > "${QUERY_DIR}/textobjects.scm" <<'Q'
; extends

; full fenced code block including fences
(fenced_code_block) @code_block.outer

; inner = block without the first and last fence lines
((fenced_code_block) @code_block.inner
  (#offset! @code_block.inner 1 0 -1 0))
Q

# Plugins (native pack/*). Clone only if missing.
[ -d "${PKG_DIR}/nvim-treesitter/.git" ]              || git clone https://github.com/nvim-treesitter/nvim-treesitter.git              "${PKG_DIR}/nvim-treesitter"
[ -d "${PKG_DIR}/nvim-treesitter-textobjects/.git" ]  || git clone https://github.com/nvim-treesitter/nvim-treesitter-textobjects.git "${PKG_DIR}/nvim-treesitter-textobjects"
[ -d "${PKG_DIR}/markview.nvim/.git" ]                || git clone https://github.com/OXY2DEV/markview.nvim.git                       "${PKG_DIR}/markview.nvim"
[ -d "${PKG_DIR}/vim-slime/.git" ]                    || git clone https://github.com/jpalardy/vim-slime.git                           "${PKG_DIR}/vim-slime"

# Treesitter parsers without prompts:
# First try update (never asks). If that fails, force install without confirmation.
if ! NVIM_APPNAME="${APPNAME}" nvim --headless "+TSUpdateSync markdown markdown_inline java" +qa; then
  NVIM_APPNAME="${APPNAME}" nvim --headless "+TSInstallSync! markdown markdown_inline java" +qa || true
fi

# Launch Neovim from the original directory; pass through user args.
NVIM_APPNAME="${APPNAME}" nvim -c "cd ${ORIG_CWD}" "$@"
