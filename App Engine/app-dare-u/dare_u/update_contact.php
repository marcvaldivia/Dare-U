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
	
	$userId = $_POST[USER_ID];
	$contactId = $_POST[CONTACT_ID];
	$del = $_POST[DEL];
	$favorite = $_POST[FAVORITE];
	$date = $_POST[DATE];
	$auth = $_POST[AUTH];
	
	$sec = new Security();
	
	if (!$sec->checkCredentials($date, $auth)) {
		exit();
	}
	
	$db = new DB_Functions();
	
	if (!isset($favorite)) $favorite = 0;
	if (!isset($del)) $del = false;
	
	$result = $db->updateContact($userId, $contactId, $favorite, $del);
	
	if ($result) {
		echo $result;
	} else {
		echo ERROR_GEN;
	}
	
?>