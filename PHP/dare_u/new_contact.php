<?php
	
	/**
	 * Imports
	 */
	require_once '../connect/db_functions.php';
    require_once '../utils/security.php';
    require_once '../constants/errors.php';
	require_once '../constants/preferences.php';
	require_once '../constants/notifications.php';
	require_once '../gcm/gcm.php';
	
	// Error reporting false
	error_reporting(0);
	
	$userId = $_POST[USER_ID];
	$username = $_POST[USERNAME];
	$contactId = $_POST[CONTACT_ID];
	$favorite = $_POST[FAVORITE];
	$notification = $_POST[NOTIFICATION];
	$date = $_POST[DATE];
	$auth = $_POST[AUTH];
	
	$sec = new Security();
	
	if (!$sec->checkCredentials($date, $auth)) {
		exit();
	}
	
	$db = new DB_Functions();
	$gcm = new GCM();
	
	if (!isset($favorite)) $favorite = 0;
	
	$result = $db->createContact($userId, $contactId, $favorite);
	
	if ($result) {
		$gcm->sendNotification($notification, $username, NOTIFICATION_CONTACT);
		echo $result;
	} else {
		echo ERROR_GEN;
	}
	
?>