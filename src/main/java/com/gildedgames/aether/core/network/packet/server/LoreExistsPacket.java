package com.gildedgames.aether.core.network.packet.server;

import com.gildedgames.aether.common.inventory.container.LoreBookContainer;
import com.gildedgames.aether.core.network.IAetherPacket.AetherPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

public class LoreExistsPacket extends AetherPacket
{
    private final int playerID;
    private final ItemStack itemStack;
    private final boolean exists;

    public LoreExistsPacket(int playerID, ItemStack itemStack, boolean exists) {
        this.playerID = playerID;
        this.itemStack = itemStack;
        this.exists = exists;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.playerID);
        buf.writeItem(this.itemStack);
        buf.writeBoolean(this.exists);
    }

    public static LoreExistsPacket decode(FriendlyByteBuf buf) {
        int playerID = buf.readInt();
        ItemStack itemStack = buf.readItem();
        boolean exists = buf.readBoolean();
        return new LoreExistsPacket(playerID, itemStack, exists);
    }

    @Override
    public void execute(Player playerEntity) {
        if (playerEntity != null && playerEntity.level != null && playerEntity.getServer() != null) {
            Entity entity = playerEntity.level.getEntity(this.playerID);
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                if (player.containerMenu instanceof LoreBookContainer) {
                    LoreBookContainer container = (LoreBookContainer) player.containerMenu;
                    container.setLoreEntryExists(this.exists);
                }
            }
        }
    }
}