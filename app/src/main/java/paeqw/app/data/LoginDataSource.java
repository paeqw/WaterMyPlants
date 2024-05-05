package paeqw.app.data;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import paeqw.app.R;
import paeqw.app.data.model.FailedToLoginException;
import paeqw.app.data.model.LoggedInUser;
import paeqw.app.ui.login.LoginActivity;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        final ResultWrapper<LoggedInUser> resultWrapper = new ResultWrapper<>();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(Executors.newSingleThreadExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User signed in successfully
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            LoggedInUser loggedInUser = new LoggedInUser(user.getUid(), user.getDisplayName());
                            resultWrapper.setResult(new Result.Success<>(loggedInUser));
                        } else {
                            // Sign in failed
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                // User does not exist, proceed with registration
                                register(username, password, resultWrapper);
                            } else {
                                // Other sign in errors
                                resultWrapper.setResult(new Result.Error(task.getException()));
                            }
                        }
                    }
                });

        return new Result.Error(new FailedToLoginException("Login failed"));
    }

    private void register(String username, String password, ResultWrapper<LoggedInUser> resultWrapper) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(Executors.newSingleThreadExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registered and signed in successfully
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            LoggedInUser loggedInUser = new LoggedInUser(user.getUid(), user.getDisplayName());
                            resultWrapper.setResult(new Result.Success<>(loggedInUser));
                        } else {
                            // Registration failed
                            resultWrapper.setResult(new Result.Error(task.getException()));
                        }
                    }
                });
    }


    // TODO: idk if logout works
    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}