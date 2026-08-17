package com.github.hhhzzzsss.songplayer;

import com.github.hhhzzzsss.songplayer.mixin.ClientPlayNetworkHandlerAccessor;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.github.hhhzzzsss.songplayer.playing.Stage;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;
import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class FakePlayerEntity extends RemotePlayer {
	public static final UUID FAKE_PLAYER_UUID = UUID.randomUUID();
    public static final Random ENTITY_ID_GENERATOR = new Random();

	LocalPlayer player = SongPlayer.MC.player;
	ClientLevel world = SongPlayer.MC.level;
	
	public FakePlayerEntity() {
		super(SongPlayer.MC.level, createProfile());
        setId(ENTITY_ID_GENERATOR.nextInt());
		
		copyStagePosAndPlayerLook();
		
		getInventory().replaceWith(player.getInventory());
		
		Byte playerModel = player.getEntityData().get(Player.DATA_PLAYER_MODE_CUSTOMISATION);
		getEntityData().set(Player.DATA_PLAYER_MODE_CUSTOMISATION, playerModel);
		
		yHeadRot = player.yHeadRot;
		yBodyRot = player.yBodyRot;

		if (player.isShiftKeyDown()) {
			setShiftKeyDown(true);
			setPose(Pose.CROUCHING);
		}

//		capeX = getX();
//		capeY = getY();
//		capeZ = getZ();
		
		world.addEntity(this);
	}
	
	public void resetPlayerPosition() {
		player.snapTo(getX(), getY(), getZ(), getYRot(), getXRot());
	}
	
	public void copyStagePosAndPlayerLook() {
		Stage lastStage = SongHandler.getInstance().lastStage;
		if (lastStage != null) {
			snapTo(lastStage.position.getX()+0.5, lastStage.position.getY(), lastStage.position.getZ()+0.5, player.getYRot(), player.getXRot());
			yHeadRot = player.yHeadRot;
		}
		else {
			copyPosition(player);
		}
	}

	private static GameProfile createProfile() {
		GameProfile profile = new GameProfile(
				FAKE_PLAYER_UUID,
				SongPlayer.MC.player.getGameProfile().name(),
				SongPlayer.MC.getGameProfile().properties()
		);
		PlayerInfo playerListEntry = new PlayerInfo(SongPlayer.MC.player.getGameProfile(), false);
		((ClientPlayNetworkHandlerAccessor)SongPlayer.MC.getConnection()).getPlayerInfoMap().put(FAKE_PLAYER_UUID, playerListEntry);
		return profile;
	}
}
