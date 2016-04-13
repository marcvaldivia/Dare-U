<?php
	
	/**
	 * Imports
	 */
    require_once '../connect/db_functions.php';
    require_once '../utils/security.php';
    require_once '../constants/errors.php';
	require_once '../constants/preferences.php';
	
	// Error reporting false
	error_reporting(0);
	
	$username = $_POST[USERNAME];
	$password = $_POST[PASSWORD];
	$device = $_POST[DEVICE];
	$IMEI = $_POST[IMEI];
	$notification = $_POST[NOTIFICATION];
	$date = $_POST[DATE];
	$auth = $_POST[AUTH];
	
	$sec = new Security();
	
	if (!$sec->checkCredentials($date, $auth)) {
		exit();
	}
	
	$username = strtolower($username);
	
	$db = new DB_Functions();
	
	$result = $db->validateUser($username, $password, true, $device, $IMEI, $notification, true);
	
	if ($result) {
		echo $result;
	} else {
		echo ERROR_GEN;
	}
	
?>