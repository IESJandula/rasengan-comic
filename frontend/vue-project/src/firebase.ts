// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAr9eIaI5Gy4Eho5gfoXGlNdmeksa00mCQ",
  authDomain: "rasengancomics-a81f8.firebaseapp.com",
  projectId: "rasengancomics-a81f8",
  storageBucket: "rasengancomics-a81f8.firebasestorage.app",
  messagingSenderId: "12217003680",
  appId: "1:12217003680:web:73aee41ee46dface3023c5",
  measurementId: "G-BBF86SKDSJ"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);