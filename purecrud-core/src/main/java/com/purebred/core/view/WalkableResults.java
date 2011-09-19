package com.purebred.core.view;

/**
 * Crud results that can be walked, i.e. navigating next/previous for edit/view.
 */
public interface WalkableResults {

    /**
     * Edit or view previous item in results.
     */
    void editOrViewPreviousItem();

    /**
     * Ask if there is a previous item available (if user hasn't already at the first item).
     */
    boolean hasPreviousItem();

    /**
     * Edit or view next item in results.
     */
    void editOrViewNextItem();

    /**
     * Ask if there is a next item available (if user hasn't already at the last item).
     */
    boolean hasNextItem();
}
