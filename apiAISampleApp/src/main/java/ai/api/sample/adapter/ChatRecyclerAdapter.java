package ai.api.sample.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ai.api.sample.R;
import ai.api.sample.model.Message;

/**
 * Created by anuj on 14/07/18.
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    public ChatRecyclerAdapter(ArrayList<Message> messageList) {
       this.messageList=messageList;
    }

    public void addMessage(Message message){
        if (messageList == null) {
            messageList = new ArrayList<>();
        }else{
            messageList.add(message);
            notifyItemInserted(messageList.size()-1);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message, parent, false);
            return new ChatRecyclerAdapter.RightViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message, parent, false);
            return new ChatRecyclerAdapter.LeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (getItemViewType(position) == 1) {
            ((RightViewHolder) holder).rightMessage.setText(message.getMessage());
        } else {
            ((LeftViewHolder) holder).leftMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class RightViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView rightMessage;

        public RightViewHolder(View itemView) {
            super(itemView);
            rightMessage = (AppCompatTextView) itemView.findViewById(R.id.message);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView leftMessage;

        public LeftViewHolder(View itemView) {
            super(itemView);
            leftMessage = (AppCompatTextView) itemView.findViewById(R.id.message);
        }
    }
}
