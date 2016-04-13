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
	
	require_once 'google/appengine/api/cloud_storage/CloudStorageTools.php';
	use google\appengine\api\cloud_storage\CloudStorageTools;
	
	// Error reporting false
	error_reporting(0);
	
	// Google Cloud Storage
	$options = [ 'gs_bucket_name' => 'app-dare-u' ];
	$upload_url = CloudStorageTools::createUploadUrl('/update_challenge.php', $options);
	
	// User challenge
	$userId = $_POST[USER_ID];
	$username = $_POST[USERNAME];
	$contactId = $_POST[CONTACT_ID];
	
	// Delete challenge
	$del = $_POST[DEL];
	
	if (!isset($del)) $del = 0;
	
	if ($del == 1) {
		$file = $_POST[FILE];
		$del = true;
	} else {
		// Answer to the challenge
		$type = $_POST[TYPE];
		$file = 'gs://app-dare-u/';
		$file .= $userId.'_'.$contactId.'_'.basename($_FILES[FILE]['name']);
		$del = false;
	}
	
	// Notification
	$notification = $_POST[NOTIFICATION];
	
	// Auth credentials
	$date = $_POST[DATE];
	$auth = $_POST[AUTH];
	
	$sec = new Security();
	
	if (!$sec->checkCredentials($date, $auth)) {
		exit();
	}
	
	$db = new DB_Functions();
	$gcm = new GCM();
	
	if ($del) {
		$result = $db->deleteChallenge($userId, $contactId);
		if ($result) {
			unlink($file);
			echo $result;
		} else {
			echo ERROR_GEN;
		}
	} else {
		if(move_uploaded_file($_FILES[FILE]['tmp_name'], $file)) {
			$result = $db->updateChallenge($userId, $contactId, $type, $file);
			if ($result) {
				$gcm->sendNotification($notification, $username, NOTIFICATION_ANSWER);
				echo $result;
			} else {
				echo ERROR_GEN;
			}	
		} else {
			echo ERROR_FILE;
		}
	}
	
?>