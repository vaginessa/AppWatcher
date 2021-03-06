package com.anod.appwatcher.sync

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.text.Html

import com.anod.appwatcher.AppWatcherActivity
import com.anod.appwatcher.NotificationActivity
import com.anod.appwatcher.R
import com.anod.appwatcher.ui.AppWatcherBaseActivity


/**
 * @author alex
 * *
 * @date 2014-09-24
 */
class SyncNotification(private val mContext: Context) {

    fun show(notification: Notification) {
        val mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancel() {
        val mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(NOTIFICATION_ID)
    }

    fun create(updatedApps: List<SyncAdapter.UpdatedApp>): Notification {
        val notificationIntent = Intent(mContext, AppWatcherActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val data = Uri.parse("com.anod.appwatcher://notification")
        notificationIntent.data = data
        notificationIntent.putExtra(AppWatcherBaseActivity.EXTRA_FROM_NOTIFICATION, true)
        val contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0)

        val title = renderNotificationTitle(updatedApps)
        val text = renderNotificationText(updatedApps)

        val builder = NotificationCompat.Builder(mContext)
        builder
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_update)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setTicker(title)

        if (updatedApps.size == 1) {
            val app = updatedApps[0]
            addExtraInfo(app, builder)
        } else {
            addMultipleExtraInfo(updatedApps, builder)
        }

        return builder.build()
    }

    private fun addMultipleExtraInfo(updatedApps: List<SyncAdapter.UpdatedApp>, builder: NotificationCompat.Builder) {
        var isUpdatable = false

        val sb = StringBuilder()
        for (app in updatedApps) {
            if (app.installedVersionCode > 0 && app.versionCode > app.installedVersionCode) {
                isUpdatable = true
            }
            sb.append(app.title).append("\n")
        }

        if (!isUpdatable) {
            return
        }

        builder.setStyle(NotificationCompat.BigTextStyle().bigText(sb.toString()))

        val updateIntent = createActionIntent(Uri.parse("com.anod.appwatcher://play/myapps/1"), NotificationActivity.TYPE_MYAPPS_UPDATE)
        builder.addAction(R.drawable.ic_system_update_alt_white_24dp, mContext.getString(R.string.noti_action_update),
                PendingIntent.getActivity(mContext, 0, updateIntent, 0)
        )

        val readIntent = createActionIntent(Uri.parse("com.anod.appwatcher://dismiss/"), NotificationActivity.TYPE_DISMISS)
        builder.addAction(R.drawable.ic_clear_white_24dp, mContext.getString(R.string.dismiss),
                PendingIntent.getActivity(mContext, 0, readIntent, 0)
        )

    }

    private fun addExtraInfo(app: SyncAdapter.UpdatedApp, builder: NotificationCompat.Builder) {
        var changes: String? = app.recentChanges
        if (changes != null) {
            if (changes == "") {
                changes = mContext.getString(R.string.no_recent_changes)
            } else {
                builder.setContentText(Html.fromHtml(changes))
            }
            builder.setStyle(
                    NotificationCompat.BigTextStyle().bigText(Html.fromHtml(changes))
            )

            val playIntent = createActionIntent(Uri.parse("com.anod.appwatcher://play/" + app.pkg), NotificationActivity.TYPE_PLAY)
            playIntent.putExtra(NotificationActivity.EXTRA_PKG, app.pkg)

            builder.addAction(R.drawable.ic_play_arrow_white_24dp, mContext.getString(R.string.store),
                    PendingIntent.getActivity(mContext, 0, playIntent, 0)
            )

            if (app.installedVersionCode > 0) {
                val updateIntent = createActionIntent(Uri.parse("com.anod.appwatcher://play/myapps/1"), NotificationActivity.TYPE_MYAPPS_UPDATE)
                builder.addAction(R.drawable.ic_system_update_alt_white_24dp, mContext.getString(R.string.noti_action_update),
                        PendingIntent.getActivity(mContext, 0, updateIntent, 0)
                )
            }

            val readIntent = createActionIntent(Uri.parse("com.anod.appwatcher://dismiss/"), NotificationActivity.TYPE_DISMISS)
            builder.addAction(R.drawable.ic_clear_white_24dp, mContext.getString(R.string.dismiss),
                    PendingIntent.getActivity(mContext, 0, readIntent, 0)
            )
        }
    }

    private fun createActionIntent(uri: Uri, type: Int): Intent {
        val intent = Intent(mContext, NotificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = uri
        intent.putExtra(NotificationActivity.EXTRA_TYPE, type)
        return intent
    }

    private fun renderNotificationText(apps: List<SyncAdapter.UpdatedApp>): String {
        val count = apps.size
        if (count == 1) {
            return mContext.getString(R.string.notification_click)
        }
        if (count > 2) {
            return mContext.getString(
                    R.string.notification_2_apps_more,
                    apps[0].title,
                    apps[1].title
            )
        }
        return mContext.getString(R.string.notification_2_apps,
                apps[0].title,
                apps[1].title
        )
    }

    private fun renderNotificationTitle(apps: List<SyncAdapter.UpdatedApp>): String {
        val title: String
        val count = apps.size
        if (count == 1) {
            title = mContext.getString(R.string.notification_one_updated, apps[0].title)
        } else {
            title = mContext.getString(R.string.notification_many_updates, count)
        }
        return title
    }

    companion object {

        internal const val NOTIFICATION_ID = 1
    }


}
