package com.dtmilano.android.culebratester2.utils;


import android.annotation.SuppressLint;
import android.util.Log;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.EventCondition;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.dtmilano.android.culebratester2.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by diego on 2017-02-13.
 * @deprecated use {@link SelectorUtilsKt}.
 */

@Deprecated
public class JavaSelectorUtils {

    private static final String TAG = "☕️JavaSelectorUtils";
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static String unescapeSelectorChars(String selector) {
        return selector.replace("\\@", "@").replaceAll("\\,", ",");
    }

    /**
     * Converts the selectorStr String to a UiSlector object.
     * <p/>
     * The format of the selectorStr string is
     * <p>
     * <pre>sel@[$]value,...</pre>
     * </p>
     * <p>
     * Where sel can be one of
     * <ul>
     * <li>clickable</li>
     * <li>depth</li>
     * <li>desc</li>
     * <li>index</li>
     * <li>instance</li>
     * <li>package</li>
     * <li>res</li>
     * <li>scrollable</li>
     * <li>text</li>
     * </ul>
     * If the first character of value is '$' then a pattern-method is used.
     * </p>
     */
    @SuppressLint("LongLogTag")
    public static UiSelectorBundle uiSelectorBundleFromString(String selectorStr) {
        UiSelector uiSelector = new UiSelector();
        // Removed the `new` for Kotlin
        //final StringBuilder sb = new StringBuilder("new UiSelector()");
        final StringBuilder sb = new StringBuilder("UiSelector()");
        // FIXME: we are still not unescaping special chars in selectorStr Patterns (when first char is '$')
        final String[] tokens = tokenize(selectorStr, "(?<!\\\\),");
        for (String token : tokens) {
            final String[] kv = tokenize(token, "(?<=[^\\\\])@");
            if (kv.length != 2) {
                Log.e(TAG, "uiSelectorBundleFromString: Malformed selectorStr, kv length != 2: " + Arrays.toString(kv));
                Log.e(TAG, "uiSelectorBundleFromString: selectorStr=" + selectorStr);
                Log.e(TAG, "uiSelectorBundleFromString: tokens: " + Arrays.toString(tokens));
                continue;
            }
            switch (kv[0]) {
                case "clazz":
                case "className":
                    final String clazz = kv[1];
                    if (clazz.charAt(0) == '$') {
                        uiSelector = uiSelector.classNameMatches(clazz.substring(1));
                        sb.append('.').append("classNameMatches(").append('"').append(clazz).append('"').append(')');
                    } else {
                        uiSelector = uiSelector.className(clazz);
                        sb.append('.').append("className(").append('"').append(clazz).append('"').append(')');
                    }
                    break;

                case "clickable":
                    final boolean isClickable = Boolean.parseBoolean(kv[1]);
                    uiSelector = uiSelector.checkable(isClickable);
                    sb.append('.').append("clickable(").append(isClickable).append(')');
                    break;

                case "depth":
                    final int depth = Integer.parseInt(kv[1]);
                    // No depth in UiSelector
                    //uiSelector.depth(depth);
                    break;

                case "desc":
                    final String contentDescription = kv[1];
                    if (contentDescription.charAt(0) == '$') {
                        // FIXME: unescaping the regular expression is a much difficult task
                        uiSelector = uiSelector.descriptionMatches(contentDescription.substring(1));
                        sb.append('.').append("descriptionMatches(").append('"').append(contentDescription).append('"').append(')');
                    } else {
                        final String unescapedContentDescription = unescapeSelectorChars(contentDescription);
                        uiSelector = uiSelector.description(unescapedContentDescription);
                        sb.append('.').append("description(").append('"').append(contentDescription).append('"').append(')');
                    }
                    break;

                case "index":
                    final int index = Integer.parseInt(kv[1]);
                    uiSelector = uiSelector.index(index);
                    sb.append('.').append("index(").append(index).append(')');
                    break;

                case "instance":
                    final int instance = Integer.parseInt(kv[1]);
                    uiSelector = uiSelector.instance(instance);
                    sb.append('.').append("instance").append(instance).append(')');
                    break;

                case "package":
                    final String packageName = kv[1];
                    uiSelector = uiSelector.packageName(packageName);
                    sb.append('.').append("packageName(").append('"').append(packageName).append('"').append(')');
                    break;

                case "parentIndex":
                    final int parentIndex = Integer.parseInt(kv[1]);
                    // Unfortunately we don't have a simple way of specifying the parent index here
//                    uiSelector = uiSelector.parentIndex(parentIndex);
//                    sb.append('.').append("parentIndex(").append(parentIndex).append(')');
                    break;

                case "res":
                    final String resourceName = kv[1];
                    uiSelector = uiSelector.resourceId(resourceName);
                    sb.append('.').append("resourceId(").append('"').append(resourceName).append('"').append(')');
                    break;

                case "scrollable":
                    final boolean isScrollable = Boolean.parseBoolean(kv[1]);
                    uiSelector = uiSelector.scrollable(isScrollable);
                    sb.append('.').append("scrollable(").append(isScrollable).append(')');
                    break;

                case "text":
                    final String text = kv[1];
                    if (text.charAt(0) == '$') {
                        final String pattern = text.substring(1);
                        uiSelector = uiSelector.textMatches(pattern);
                        sb.append('.').append("textMatches(").append('"').append(pattern).append('"').append(')');
                    } else {
                        final String unescapedText = unescapeSelectorChars(text);
                        uiSelector = uiSelector.text(unescapedText);
                        sb.append('.').append("text(").append('"').append(unescapedText).append('"').append(')');
                    }
                    break;

                default:
                    Log.e(TAG, "Unknown selectorStr: " + Arrays.toString(kv));
            }
        }
        if (DEBUG) {
            Log.w(TAG, "uiSelectorBundleFromString: '" + selectorStr + "' => " + uiSelector);
        }

        return new UiSelectorBundle(uiSelector, selectorStr, sb.toString());
    }

    @NotNull
    public static String[] tokenize(String selector, String s) {
        return selector.split(s);
    }

    /**
     * Converts the selectorStr String to a BySlector object.
     * <p/>
     * The format of the selectorStr string is
     * <p>
     * <pre>sel@[$]value,...</pre>
     * </p>
     * <p>
     * Where sel can be one of
     * <ul>
     * <li>checkable</li>
     * <li>clazz</li>
     * <li>clickable</li>
     * <li>depth</li>
     * <li>desc</li>
     * <li>package</li>
     * <li>res</li>
     * <li>scrollable</li>
     * <li>text</li>
     * </ul>
     * If the first character of value is '$' then a Pattern is created.
     * </p>
     */
    @SuppressLint("LongLogTag")
    public static BySelectorBundle bySelectorBundleFromString(String selectorStr) {
        // TRICK: because BySelector constructor is not public we create an "always match" condition
        // FIXME: we can get rid of the checks for 'null'
        BySelector bySelector = null; //By.clazz(Pattern.compile(".*"));
        final StringBuilder sb = new StringBuilder(); // new StringBuilder("By.clazz(Pattern.compile(\".*\"))");
        // FIXME: we are still not unescaping special chars in selectorStr Patterns (when first char is '$')
        final String[] tokens = tokenize(selectorStr, "(?<!\\\\),");
        for (String token : tokens) {
            final String[] kv = tokenize(token, "(?<=[^\\\\])@");
            if (kv.length != 2) {
                Log.e(TAG, "Malformed selectorStr: " + Arrays.toString(kv));
                continue;
            }
            switch (kv[0]) {
                case "checkable":
                    final boolean isCheckable = Boolean.parseBoolean(kv[1]);
                    if (bySelector == null) {
                        bySelector = By.checkable(isCheckable);
                        sb.append("By.checkable(").append(isCheckable).append(")");
                    } else {
                        bySelector.checkable(isCheckable);
                        sb.append(".checkable(").append(isCheckable).append(')');
                    }
                    break;

                case "clazz":
                    final String clazz = kv[1];
                    if (clazz.charAt(0) == '$') {
                        final Pattern pattern = Pattern.compile(clazz.substring(1));
                        if (bySelector == null) {
                            bySelector = By.clazz(pattern);
                            sb.append("By.clazz(Pattern.compile(\"").append(pattern).append(")");
                        } else {
                            bySelector.clazz(pattern);
                            sb.append(".clazz(Pattern.compile(\"").append(pattern).append("\")");
                        }
                    } else {
                        final String unescapedClazz = unescapeSelectorChars(clazz);
                        if (bySelector == null) {
                            bySelector = By.clazz(unescapedClazz);
                            sb.append("By.clazz(\"").append(unescapedClazz).append("\")");
                        } else {
                            bySelector.clazz(unescapedClazz);
                            sb.append(".clazz(\"").append(unescapedClazz).append("\")");
                        }
                    }
                    break;

                case "clickable":
                    final boolean isClickable = Boolean.parseBoolean(kv[1]);
                    if (bySelector == null) {
                        bySelector = By.clickable(isClickable);
                        sb.append("By.clickable(").append(isClickable).append(')');
                    } else {
                        bySelector.clickable(isClickable);
                        sb.append(".clickable(").append(isClickable).append(')');
                    }
                    break;

                case "depth":
                    final int depth = Integer.parseInt(kv[1]);
                    if (bySelector == null) {
                        bySelector = By.depth(depth);
                        sb.append("By.depth(").append(depth).append(')');
                    } else {
                        bySelector.depth(depth);
                        sb.append(".depth(").append(depth).append(')');
                    }
                    break;

                case "desc":
                    final String contentDescription = kv[1];
                    if (contentDescription.charAt(0) == '$') {
                        final Pattern pattern = Pattern.compile(contentDescription.substring(1));
                        if (bySelector == null) {
                            bySelector = By.desc(pattern);
                            sb.append("By.desc(Pattern.compile(\"").append(pattern).append("\")");
                        } else {
                            bySelector.desc(pattern);
                            sb.append(".desc(Pattern.compile(\"").append(pattern).append("\")");
                        }
                    } else {
                        final String unescapedContentDescription = unescapeSelectorChars(contentDescription);
                        if (bySelector == null) {
                            bySelector = By.desc(unescapedContentDescription);
                            sb.append("By.desc(\"").append(unescapedContentDescription).append("\")");
                        } else {
                            bySelector.desc(unescapedContentDescription);
                            sb.append(".desc(\"").append(unescapedContentDescription).append("\")");
                        }
                    }
                    break;

                case "package":
                    final String packageName = kv[1];
                    if (packageName.charAt(0) == '$') {
                        final Pattern pattern = Pattern.compile(packageName.substring(1));
                        if (bySelector == null) {
                            bySelector = By.pkg(pattern);
                            sb.append("By.pkg(Pattern.compile(\"").append(pattern).append("\")");
                        } else {
                            bySelector.pkg(pattern);
                            sb.append(".pkg(Pattern.compile(\"").append(pattern).append("\")");
                        }
                    } else {
                        if (bySelector == null) {
                            bySelector = By.pkg(packageName);
                            sb.append("By.pkg(\"").append(packageName).append("\")");
                        } else {
                            bySelector.pkg(packageName);
                            sb.append(".pkg(\"").append(packageName).append("\")");
                        }
                    }
                    break;

                case "res":
                    final String resourceName = kv[1];
                    if (bySelector == null) {
                        bySelector = By.res(resourceName);
                        sb.append("By.res(\"").append(resourceName).append("\")");
                    } else {
                        bySelector.res(resourceName);
                        sb.append(".res(\"").append(resourceName).append("\")");
                    }
                    break;

                case "scrollable":
                    final boolean isScrollable = Boolean.parseBoolean(kv[1]);
                    if (bySelector == null) {
                        bySelector = By.scrollable(isScrollable);
                        sb.append("By.scrollable(").append(isScrollable).append(")");
                    } else {
                        bySelector.scrollable(isScrollable);
                        sb.append(".scrollable(").append(isScrollable).append(")");
                    }
                    break;

                case "text":
                    final String text = kv[1];
                    if (text.charAt(0) == '$') {
                        final Pattern pattern = Pattern.compile(text.substring(1));
                        if (bySelector == null) {
                            bySelector = By.text(pattern);
                            sb.append("By.text(Pattern.compile(\"").append(pattern).append("\"))");
                        } else {
                            bySelector.text(pattern);
                            sb.append(".text(Pattern.compile(\"").append(pattern.toString()).append("\"))");
                        }
                    } else {
                        final String unescapedText = unescapeSelectorChars(text);
                        if (bySelector == null) {
                            bySelector = By.text(unescapedText);
                            sb.append("By.text(\"").append(text).append("\")");
                        } else {
                            bySelector.text(unescapedText);
                            sb.append(".text(\"").append(text).append("\")");
                        }
                    }
                    break;

                default:
                    Log.e(TAG, "Unknown selectorStr: " + Arrays.toString(kv));
            }
        }
        if (DEBUG) {
            Log.w(TAG, "bySelectorFromString: '" + selectorStr + "' => " + bySelector);
        }
        return new BySelectorBundle(bySelector, selectorStr, sb.toString());
    }

    /**
     * Converts the provided String into an {@link EventCondition}.
     *
     * @param eventConditionStr the eventCondition String. The String can a be list of conditions
     *                          separated by <code>,</code> (comma).
     *                          The supported conditions are:
     *                          <ul>
     *                          <li>until:</li>
     *                          <ul>
     *                          <li>newWindow</li>
     *                          <li>scrollFinished</li>
     *                          </ul>
     *                          </ul>
     * @return the converted {@link EventCondition}
     */
    public static EventCondition<Boolean> eventConditionFromString(String eventConditionStr) {
        EventCondition<Boolean> eventCondition = null;

        final String[] tokens = tokenize(eventConditionStr, "(?<!\\\\),");
        for (String token : tokens) {
            final String[] ccv = tokenize(token, "(?<=[^\\\\]):");
            switch (ccv[0]) {
                case "until":
                    final Until until = new Until();
                    final String[] cv = tokenize(ccv[1], "@");
                    switch (cv[0]) {
                        case "newWindow":
                            eventCondition = Until.newWindow();
                            break;

                        case "scrollFinished":
                            eventCondition = Until.scrollFinished(Direction.valueOf(cv[1]));
                            break;
                    }
                    break;

                default:
                    // Unknown condition
            }
        }

        return eventCondition;
    }
}

