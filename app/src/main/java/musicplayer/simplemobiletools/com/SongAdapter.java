package musicplayer.simplemobiletools.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater inflater;

    public SongAdapter(Context context, ArrayList<Song> songs) {
        this.songs = songs;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.song, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Song song = songs.get(pos);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());

        return view;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return songs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder {
        @Bind(R.id.songTitle) TextView title;
        @Bind(R.id.songArtist) TextView artist;

        private ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
