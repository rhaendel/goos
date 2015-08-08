#!/bin/sh -e
pwd=$(pwd)

server_dir="$pwd/server"
openfire_dir="$server_dir/openfire"
openfire_tgz="openfire_3_6_0a.tar.gz"
setup_done="$server_dir/setup.done"
pidfile="$server_dir/openfire.pid"
cookies="$server_dir/cookies"

start_server()
{
  echo "\n [ Starting Openfire Server... ]\n"

  cd $pwd
  java -server -DopenfireHome="$openfire_dir" -Dopenfire.lib.dir="$openfire_dir/lib" -jar "$openfire_dir/lib/startup.jar" > "$server_dir/openfire.log" &
  pid=$!
  echo $pid > "$pidfile"
  while ! curl "http://localhost:9090/login.jsp" -H "Content-Type: application/x-www-form-urlencoded" --data "url="%"2Findex.jsp&login=true&username=admin&password=admin" -c "$cookies" ; do
    echo "...waiting for startup..."
    sleep 1
  done
  cat "$server_dir/openfire.log" || true
  echo "\nUse 'kill \$(cat $pidfile)' to shutdown Openfire!\n"
}

download_and_unpack()
{
  echo "\n [ Downloading and unpacking openfire... ]\n"

  mkdir -p "$server_dir"
  cd "$server_dir"
  wget "http://www.igniterealtime.org/downloadServlet?filename=openfire/$openfire_tgz" -O "$openfire_tgz"
  tar xzf "$openfire_tgz"
  curl -o "$openfire_dir/conf/openfire.xml" "https://raw.githubusercontent.com/rhaendel/goos-infra/master/src/openfire.xml"
}

configure_openfire()
{
  echo "\n [ Configuring Openfire... ]\n"
  {
    cd "$server_dir"
    curl "http://localhost:9090/offline-messages.jsp?quota=100.00&strategy=2&update=Save+Settings" -b "$cookies"
    curl "http://localhost:9090/user-create.jsp?username=sniper&name=&email=&password=sniper&passwordConfirm=sniper&create=Create+User" -b "$cookies"
    curl "http://localhost:9090/user-create.jsp?username=auction-item-54321&name=&email=&password=auction&passwordConfirm=auction&create=Create+User" -b "$cookies"
    curl "http://localhost:9090/user-create.jsp?username=auction-item-65432&name=&email=&password=auction&passwordConfirm=auction&create=Create+User" -b "$cookies"
  } > "$server_dir/setup.log" 2>&1
}

if [ -f "$setup_done" ] ; then
  start_server
else
  download_and_unpack
  start_server
  configure_openfire
  touch "$setup_done"
fi

echo "\nDONE"
