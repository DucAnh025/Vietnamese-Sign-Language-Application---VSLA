// Generated by view binder compiler. Do not edit!
package com.google.mediapipe.examples.gesturerecognizer.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.mediapipe.examples.gesturerecognizer.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemQuizzesBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final RadioButton answerA;

  @NonNull
  public final RadioButton answerB;

  @NonNull
  public final RadioButton answerC;

  @NonNull
  public final RadioButton answerD;

  @NonNull
  public final RadioGroup answerGroup;

  @NonNull
  public final ImageView questionImage;

  @NonNull
  public final TextView questionText;

  private ItemQuizzesBinding(@NonNull CardView rootView, @NonNull RadioButton answerA,
      @NonNull RadioButton answerB, @NonNull RadioButton answerC, @NonNull RadioButton answerD,
      @NonNull RadioGroup answerGroup, @NonNull ImageView questionImage,
      @NonNull TextView questionText) {
    this.rootView = rootView;
    this.answerA = answerA;
    this.answerB = answerB;
    this.answerC = answerC;
    this.answerD = answerD;
    this.answerGroup = answerGroup;
    this.questionImage = questionImage;
    this.questionText = questionText;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemQuizzesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemQuizzesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_quizzes, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemQuizzesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.answerA;
      RadioButton answerA = ViewBindings.findChildViewById(rootView, id);
      if (answerA == null) {
        break missingId;
      }

      id = R.id.answerB;
      RadioButton answerB = ViewBindings.findChildViewById(rootView, id);
      if (answerB == null) {
        break missingId;
      }

      id = R.id.answerC;
      RadioButton answerC = ViewBindings.findChildViewById(rootView, id);
      if (answerC == null) {
        break missingId;
      }

      id = R.id.answerD;
      RadioButton answerD = ViewBindings.findChildViewById(rootView, id);
      if (answerD == null) {
        break missingId;
      }

      id = R.id.answerGroup;
      RadioGroup answerGroup = ViewBindings.findChildViewById(rootView, id);
      if (answerGroup == null) {
        break missingId;
      }

      id = R.id.questionImage;
      ImageView questionImage = ViewBindings.findChildViewById(rootView, id);
      if (questionImage == null) {
        break missingId;
      }

      id = R.id.questionText;
      TextView questionText = ViewBindings.findChildViewById(rootView, id);
      if (questionText == null) {
        break missingId;
      }

      return new ItemQuizzesBinding((CardView) rootView, answerA, answerB, answerC, answerD,
          answerGroup, questionImage, questionText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
