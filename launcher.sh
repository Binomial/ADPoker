#!/bin/bash
cd build/classes/
if [ $# -eq 0 ]; then	
	java reso.LauncherReso
	echo "Serveur lancé"
else
	java adPoker.Joueur $1 $2 $3
	echo "Client $1 lancé"
fi
