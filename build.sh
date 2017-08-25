#!/bin/bash

export LC_ALL=C

if [ "`uname -s`" == "Darwin" ]
  then
    python=`which python2`
    $python --version > /dev/null 2>&1
    if [ "$?" != "0" ]
      then printf '\nNo valid python2 found.\n\nInstall the required packages by installing Brew.\n\n\t/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"\n\tbrew install python pyenv-virtualenv\n\n'; exit 1
    fi
fi

if [ "`uname -s`" == "Linux" ]
  then
    python=`which python2`
    $python --version > /dev/null 2>&1
    if [ "$?" != "0" ]
      then printf "\nNo valid python installed, install python2\n\n"; exit 1
    fi
fi

if [ "$?" = "0" ]
  then
    if [ ! -f ".venv/bin/activate" ]
      then
        virtualenv -p python2 .venv
        ./.venv/bin/pip install -r requirements.txt
    fi
    ansible-playbook builder.yml
  else
    printf "Python2 is required\n"
    exit 1
fi
