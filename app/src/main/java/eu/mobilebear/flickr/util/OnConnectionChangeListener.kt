package eu.mobilebear.flickr.util

interface OnConnectionChangeListener {

    fun onConnectionLost()

    fun onConnectionReestablished()

    fun onConnectionStatusDetected(connected: Boolean)
}
