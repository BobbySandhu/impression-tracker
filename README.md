## ImpressionTracker

This _Impression Tracker_ would help you track RecyclerView impressions when scrolled. It can help you track any nested horizontal recyclerview also.

Impression Tracking works for a recyclerview where the visibility percentage defined by the user/developer is met. When this visibility threshold is achieved for an item on the screen a callback is returned that you can use to perform your required task.

### Usage

There are basically two types of use cases.

*   _Tracking the impression in the vertical recyclerview_ just like every app, e.g. FB, Insta, Twitter etc.
*   _Tracking the impression in inner recyclerview_ (inner horizontal reyclerview), e.g. PlayStore App, Spotify etc.

You need to first create an object of _ImpressionTracker(recyclerView, visibilityPercentage, ImpressionTrackerListener)_. Which takes three params:

> _**recyclerViewVertical:**_ your main recyclerview in the app.

> _**visibilityPercentage:**_ visibility percentage that you want to use as threshold in your main recyclerview inside the app.

> _**ImpressionTrackerListener:**_ listener to listen for different callbacks.

**1\.** _**Tracking the impression in the vertical recyclerview**_**:** This is how you would implement the impression tracking in vertical recyclerview use-case.

```plaintext
impressionTracker = ImpressionTracker(binding.recyclerViewVertical, 40, object : ImpressionTrackerListener {    

override fun onVerticalItem(position: Int, viewHolder: RecyclerView.ViewHolder?) {    
    /* use as per your use case */    
}    

override fun onHorizontalItem(parentPosition: Int, childPosition: Int) {    
    /* use as per your use case */    
}    

override fun onVerticalItemVisibility(visibility: Double, position: Int) {    
    adapter?.updateData(items[position].copy(visibility = visibility.toInt()), position)    

    if (isFirstLoad) {//added a delay to refresh the recycler, as it doesn't update when it's getting set.    
       handler.postDelayed({    
          adapter?.notifyItemChanged(position)    
            isFirstLoad = false    
        }, 100)    
    } else adapter?.notifyItemChanged(position)    
}    

override fun onHorizontalItemVisibility(    
    visibility: Double, 
    parentPosition: Int,        
    childPosition: Int    
) {    
    /* use as per your use case */    
  }    
}) 
```

![](https://github.com/BobbySandhu/impression-tracker/blob/dev/videos/vertical.gif)

**2\.** _**Tracking the impression in inner recyclerview**_**:** This is how you would implement the impression tracking in the inner horizontal recyclerview use-case. In this case two callbacks are used to track the impression.

After vertical recyclerview is visible with the given threshold, it returns the viewholder of the currently visible item. We have to use it to get the inner recyclerview and call its tracking.

```plaintext
impressionTracker = ImpressionTracker(binding.recyclerViewVertical, 40, object : ImpressionTrackerListener {    

override fun onVerticalItem(position: Int, viewHolder: RecyclerView.ViewHolder?) {    
    /* activating/adding inner horizontal recycler impression tracking.  
    * horizontal item's callback is received in onHorizontalItemVisibility() and you will have to override it.        
    **/    
    if (viewHolder is VerticalRecyclerAdapter.VisibilityViewHolder) {    
        impressionTracker?.trackHorizontalRecyclerView(viewHolder.binding.recyclerViewInner, 40, position)    
    }  
}    

override fun onHorizontalItem(parentPosition: Int, childPosition: Int) {    
    /* use as per your use case */    
}    
    
override fun onVerticalItemVisibility(visibility: Double, position: Int) {    
    /* use as per your use case */  
}    

override fun onHorizontalItemVisibility(    
    visibility: Double,  
 	parentPosition: Int,        
 	childPosition: Int    
) {    
     //updating data in inner recyclerview    
     val innerData = items[parentPosition].innerData    
     val uiData = innerData[childPosition].copy(visibility = visibility.toInt())    
     adapter?.updateHorizontalItem(uiData, childPosition, parentPosition)   
   }    
})  
```

![](https://github.com/BobbySandhu/impression-tracker/blob/dev/videos/vertical%20+%20horizontal.gif)

### Callbacks

There are four callbacks available for different use-cases. While adding a listener you can implement the only those methods that suits your requirements.

> In case if you have a different use-case then you can either raise a PR if you have already covered it or you can request one. If the requested use-case is generic one which others can benefit from, I'll try to cover it.

**onVerticalItem(position: Int, viewHolder: RecyclerView.ViewHolder?):** This method returns the position and viewHolder of your vertical recyclerview's item when it is visible, after your provided visibility threshold is met.

You can use this ViewHolder to further add tracking to any inner horizontal recyclerview inside a specific type of ViewHolder in case of multiple view holder adapter or perform any action on any specific type of view.

**onHorizontalItem(parentPosition: Int, childPosition: Int):** This method can be used when you have added tracking for the internal horizontal recyclerView. When the provided visibility threshold for inner item is met. It returns the position of the parent item and inner item's position in it after scroll ends.

**onVerticalItemVisibility(visibility: Double, position: Int):** When instead of your provided visibility threshold you still want to track the current visibility percentage and perform a specific action, you can use this method. It returns the current visibility percentage of vertical recyclerview's item and it's position in recyclerview.

**onHorizontalItemVisibility(visibility: Double, parentPosition: Int, childPosition: Int):** This method is similar to _onVerticalItemVisibility(visibility: Double, position: Int)_ but for inner horizontal recycler item.

> This library has no performance issues and is optimized for scrolling. In case if you find any issue, you can report it.

### Implementation

```plaintext
// Top level build file 
repositories { // Add this to the end of any existing repositories
	mavenCentral()
}

// app level dependencies section
dependencies {
	implementation 'io.github.bobbysandhu:WIP'
}
```

    You can contribute and help the community. All you need to do is share your PR with clear message. That's it!!