This android application can be used as a lightweight SMS gateway on your android device.

It basically forwards any sms message received through the device to a defined URL.
In the setting, you can also define a secret which can be used to authenticate requests.

The receiving web application script must return an 'ok' response to acknowlege receipt or else, the gateway will keep trying to push the SMS to the server.

It also implements an sqlite database on device to store the messages in case of a network failure.

You can also export messages from the database to CSV on your memory card for download through USB.

The phone is also aware of multiple SIMS on the phone so it can receive messages on the different SIMS and forward to different URLS. Essentially, it means the app can be used to host two SIMS for two different applications entirely.

Sample PHP receiver Script
```php

define("SMS_SECRET", "xxxxxx");
$data = $_POST;

/*$data = array("Secret" => "xxxxxx",
			"id" => "10",						
			"sender" => "+234...",
			"date" => "2014-07-21 03:30:20",
			"message" => "Test Message"); */

if (@$data["Secret"] != SMS_SECRET) die("ok");


@mysql_select_db("sms_db", @mysql_connect("localhost", "username", "password"));

if (@mysql_query("INSERT INTO incoming_message(phone, message, date_added) " .
	"VALUES('" . addslashes($data['sender']) . "', '" . addslashes($data['message']) . "', '" . addslashes($data['date']) . "')")) {
	echo "ok";
}

```