$(document).ready(() => {
  // Your web app's Firebase configuration
  var firebaseConfig = {
    apiKey: "AIzaSyBRRqudJsKGAtpUwkecr1kQu2wFOyRMGZY",
    authDomain: "top-50-9951b.firebaseapp.com",
    databaseURL: "https://top-50-9951b.firebaseio.com",
    projectId: "top-50-9951b",
    storageBucket: "top-50-9951b.appspot.com",
    messagingSenderId: "692761073073",
    appId: "1:692761073073:web:4d5510c333590810e51990",
    measurementId: "G-87W55FE9RG",
  };

  // Initialize Firebase
  if (firebase.apps.length == 0) {
    firebase.initializeApp(firebaseConfig);
  }
});
