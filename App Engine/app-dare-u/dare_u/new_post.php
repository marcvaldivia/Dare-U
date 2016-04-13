<?php
	
	/**
	 * Imports
	 */
	require_once '../connect/db_functions.php';
    require_once '../utils/security.php';
    require_once '../constants/errors.php';
	require_once '../constants/preferences.php';
	
	require_once 'google/appengine/api/cloud_storage/CloudStorageTools.php';
	use google\appengine\api\cloud_storage\CloudStorageTools;
	
	// Error reporting false
	error_reporting(0);
	
	// Google Cloud Storage
	$options = [ 'gs_bucket_name' => 'app-dare-u' ];
	$upload_url = CloudStorageTools::createUploadUrl('/new_post.php', $options);
	
	$userId = $_POST[USER_ID];
	$username = $_POST[USERNAME];
	$contactId = $_POST[CONTACT_ID];
	$contact = $_POST[CONTACT];
	$challenge = $_POST[CHALLENGE];
	$type = $_POST[TYPE];
	
	$date = $_POST[DATE];
	$auth = $_POST[AUTH];
	
	$sec = new Security();
	
	if (!$sec->checkCredentials($date, $auth)) {
		exit();
	}
	
	$db = new DB_Functions();
	
	$challenge = htmlentities($challenge);
	$file = 'gs://app-dare-u/';
	$file .= 'POST_'.$userId.'_'.basename($_FILES[FILE]['name']);
	
	if(move_uploaded_file($_FILES[FILE]['tmp_name'], $file)) {
		$result = $db->createPost($userId, $username, $contactId, $contact, $challenge, $type, $file);
		if ($result) {
			echo $result;
		} else {
			echo ERROR_GEN;
		}	
	} else{
		echo ERROR_FILE;
	}
	
?>