#!/usr/bin/env sh

BASE_DIR="$(cd "$(dirname "$0")"; pwd)" || exit 2

mkdir -p $HOME/.local/share/applications

cat <<EOF > $HOME/.local/share/applications/Toadpen.desktop
[Desktop Entry]
Name=Toadpen++
Exec=$BASE_DIR/toadpen.sh
Icon=$BASE_DIR/toadpen.png
Terminal=false
Type=Application
Keywords=toadpen;editor;text;code;
EOF
