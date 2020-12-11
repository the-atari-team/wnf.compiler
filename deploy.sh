#!/bin/bash

function copyJar(){
    SRC=$1
    DEST=$2

    if [[ -e ${JAR} ]]; then
        cp ${JAR} ${DEST}
        echo "Now use it with wnfc ..."
    else
        echo "Can't find JAR: ${JAR}, forget to build?"
    fi
}

function createQuickStarter() {
    local LOCALPATH=${1}
    cat >${LOCALPATH}/wnfc <<EOF
#!/bin/bash
SCRIPT=\$(dirname \$0)
java -jar \${SCRIPT}/wnf-compiler.jar \$@
EOF
}

JAR="compiler/target/wnf-compiler.jar"

if [[ "$OS" == "Windows_NT" ]]; then
    echo "found Windows NT"
    UNIXPROFILE=$(cygpath -u $USERPROFILE)
    if [[ -d "$UNIXPROFILE/bin" ]]; then
    	DEST="$UNIXPROFILE/bin"
    else
      echo "Copy $JAR to a PATH directory where you want."
      exit 1
    fi
    copyJar $JAR $DEST
    createQuickStarter $DEST

else
    if [[ -d "/home/${USER}/.local/bin" ]]; then
        copyJar $JAR /home/${USER}/.local/bin

        echo "use it with wnfc"
        createQuickStarter /home/${USER}/.local/bin
        chmod +x /home/${USER}/.local/bin/wnfc

    elif [[ -d "/home/${USER}/bin" ]]; then
        copyJar $JAR /home/${USER}/bin

        echo "use it with wnfc"
        createQuickStarter /home/${USER}/bin
        chmod +x /home/${USER}/bin/wnfc

    else
        echo "ERROR: There exists no ~/bin nor ~/.local/bin directory"
        exit 1
    fi
fi
